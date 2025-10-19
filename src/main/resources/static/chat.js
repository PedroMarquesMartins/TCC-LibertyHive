const token = localStorage.getItem('token');
const userIdLogado = localStorage.getItem('userId');
const userNomeLogado = localStorage.getItem('userNome');

if (!token || !userIdLogado) {
    document.body.innerHTML = '<div style="padding:30px;text-align:center;"><h2>Você precisa estar logado.</h2></div>';
    throw new Error('Usuário não logado');
}

const chatTitle = document.getElementById('chatTitle');
const chatArea = document.getElementById('chatArea');
const msgInput = document.getElementById('msgInput');
const btnEnviar = document.getElementById('btnEnviar');
const btnBloquear = document.getElementById('btnBloquear');
const btnProposta = document.getElementById('btnProposta');
const btnVoltar = document.getElementById('btnVoltar');
const participantesEl = document.getElementById("participantes");
const valorPropostaEl = document.getElementById("valorProposta");

const urlParams = new URLSearchParams(window.location.search);
const chatId = urlParams.get('chatId');
if (!chatId) throw new Error('ChatId ausente');

let chatBloqueado = false;

function formatarDataHora(dtStr) {
    const dt = new Date(dtStr);
    return dt.toLocaleDateString('pt-BR') + ' ' + dt.toLocaleTimeString('pt-BR');
}

async function carregarChat() {
    try {
        const resp = await fetch(`http://localhost:8080/chat/${chatId}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!resp.ok) throw new Error("Erro ao carregar chat.");
        const chat = await resp.json();

        const outroNome = (userIdLogado == chat.userId01) ? chat.userNome02 : chat.userNome01;
        chatTitle.textContent = `Chat com ${outroNome}`;

        participantesEl.textContent = `${chat.userNome01} e ${chat.userNome02}`;
        valorPropostaEl.textContent = chat.valorProposto ?? 'Não definido';

        chatBloqueado = chat.bloqueado === true;
        btnBloquear.textContent = chatBloqueado ? "Desbloquear" : "Bloquear";

        const respMsgs = await fetch(`http://localhost:8080/chat/${chatId}/mensagens`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        let mensagens = [];
        if (respMsgs.ok) mensagens = await respMsgs.json();

        chatArea.innerHTML = ""; 
        mensagens.forEach(m => {
            const div = document.createElement("div");

            if (m.sistema) {
                div.className = "mensagem proposta";
                div.innerHTML = `${m.mensagem}`;
            } else {
                div.className = (m.userId == userIdLogado) ? "mensagem eu" : "mensagem outro";
                const nome = (m.userId == userIdLogado) ? 'Você' : (m.userId == chat.userId01 ? chat.userNome01 : chat.userNome02);
                const dataHora = m.dataHora ? formatarDataHora(m.dataHora) : '';
                div.innerHTML = `<strong>${nome}:</strong> ${m.mensagem}<span class="dataHora">${dataHora}</span>`;
            }

            chatArea.appendChild(div);
        });

        chatArea.scrollTop = chatArea.scrollHeight;
    } catch (err) {
        console.error("Erro ao carregar chat:", err);
        chatArea.innerHTML = "<p style='color:red;'>Erro ao carregar chat.</p>";
    }
}

carregarChat();
setInterval(carregarChat, 3000);

btnEnviar.addEventListener("click", async () => {
    const msg = msgInput.value.trim();
    if (!msg) return;

    if (chatBloqueado) {
        Swal.fire("Chat bloqueado", "Não é possível enviar mensagens neste chat.", "warning");
        return;
    }

    try {
        const resp = await fetch(`http://localhost:8080/chat/${chatId}/mensagem`, {
            method: "POST",
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
            body: JSON.stringify({ mensagem: msg })
        });
        if (resp.ok) {
            msgInput.value = "";
            carregarChat();
        } else {
            Swal.fire("Erro", "Não foi possível enviar a mensagem.", "error");
        }
    } catch (err) {
        console.error(err);
        Swal.fire("Erro", "Não foi possível enviar a mensagem.", "error");
    }
});

btnBloquear.addEventListener("click", async () => {
    const confirm = await Swal.fire({
        title: `Você quer ${chatBloqueado ? 'desbloquear' : 'bloquear'} este chat?`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sim',
        cancelButtonText: 'Não'
    });

    if (confirm.isConfirmed) {
        try {
            const resp = await fetch(`http://localhost:8080/chat/${chatId}/bloquear`, {
                method: "PUT",
                headers: { 'Authorization': 'Bearer ' + token }
            });
            if (resp.ok) {
                const text = await resp.text();
                Swal.fire("Sucesso", text, "success");
                carregarChat();
            } else {
                Swal.fire("Erro", "Não foi possível alterar o bloqueio.", "error");
            }
        } catch (err) {
            console.error(err);
            Swal.fire("Erro", "Erro ao alterar o bloqueio.", "error");
        }
    }
});

btnProposta.addEventListener("click", async () => {
    const { value: valor } = await Swal.fire({
        title: 'Insira o valor da proposta',
        input: 'number',
        inputLabel: 'Valor',
        inputPlaceholder: 'R$',
        showCancelButton: true,
        inputValidator: (v) => !v || v <= 0 ? 'Informe um valor válido' : null
    });

    if (valor) {
        try {
            const resp = await fetch(`http://localhost:8080/chat/${chatId}/valor`, {
                method: "PUT",
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
                body: JSON.stringify({ valorProposto: parseFloat(valor) })
            });

            if (resp.ok) {
                Swal.fire("Sucesso", "Valor atualizado!", "success");
                carregarChat();
                const respMsg = await fetch(`http://localhost:8080/chat/${chatId}/mensagem`, {
                    method: "POST",
                    headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
                    body: JSON.stringify({
                        mensagem: `💰 Enviou uma proposta de R$ ${parseFloat(valor).toFixed(2).replace('.', ',')} reais. 💰`,
                        sistema: true
                    })
                });

                if (!respMsg.ok) {
                    console.warn("Não foi possível registrar a mensagem de proposta.");
                } else {
                    carregarChat();
                }

            } else {
                Swal.fire("Erro", "Não foi possível atualizar o valor.", "error");
            }
        } catch (err) {
            console.error(err);
            Swal.fire("Erro", "Erro ao atualizar valor.", "error");
        }
    }
});

btnVoltar.addEventListener("click", () => {
    window.location.href = `contatos.html?token=${encodeURIComponent(token)}`;
});

msgInput.addEventListener("keydown", function (e) {
    if (e.key === "Enter") {
        e.preventDefault();
        btnEnviar.click();
    }
});

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userNome');
    window.location.href = 'login.html';
}