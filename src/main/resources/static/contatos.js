document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const userIdLogado = localStorage.getItem("userId");
    const userNomeLogado = localStorage.getItem("userNome");

    if (!token || !userIdLogado || !userNomeLogado) {
        document.body.innerHTML = `
            <div style="padding:30px;text-align:center;">
                <h2>Você precisa estar logado.</h2>
            </div>`;
        return;
    }

    console.log("Usuário logado:", userIdLogado, userNomeLogado);
    console.log("Token carregado:", token);

    const listaContatos = document.getElementById("listaContatos");

    async function carregarContatos() {
        try {
            const resp = await fetch(`http://localhost:8080/chat/usuario/${userIdLogado}`, {
                headers: {
                    "Authorization": "Bearer " + token
                }
            });

            if (resp.status === 401) {
                throw new Error("Token inválido ou expirado. Faça login novamente.");
            }

            if (!resp.ok) {
                throw new Error(`Erro ao carregar contatos: ${resp.status}`);
            }

            const chats = await resp.json();

            if (chats.length === 0) {
                listaContatos.innerHTML = "<p>Nenhum chat encontrado.</p>";
                return;
            }

            listaContatos.innerHTML = "";

            for (const chat of chats) {
                const outroNome = (chat.userId01 == userIdLogado)
                    ? chat.userNome02
                    : chat.userNome01;

                let ultimaMsg = "Sem mensagens ainda";
                let dataMsg = "";

                try {
                    const msgResp = await fetch(`http://localhost:8080/chat/${chat.id}/mensagens`, {
                        headers: {
                            "Authorization": "Bearer " + token
                        }
                    });

                    if (msgResp.ok) {
                        const mensagens = await msgResp.json();
                        if (mensagens.length > 0) {
                            const ultima = mensagens[mensagens.length - 1];
                            const quemEnviou =
                                ultima.userId == userIdLogado ? "Você" :
                                (ultima.userId == chat.userId01 ? chat.userNome01 : chat.userNome02);

                            ultimaMsg = `${quemEnviou}: ${ultima.mensagem}`;
                            dataMsg = ultima.dataHora
                                ? new Date(ultima.dataHora).toLocaleString("pt-BR", {
                                      dateStyle: "short",
                                      timeStyle: "short"
                                  })
                                : "";
                        }
                    }
                } catch (e) {
                    console.warn("Erro ao buscar mensagens do chat:", e);
                }

                const div = document.createElement("div");
                div.className = "contato-card";

                div.innerHTML = `
                    <div class="contato-info">
                        <img src="https://cdn-icons-png.flaticon.com/512/149/149071.png" alt="perfil" class="contato-foto">
                        <div class="contato-detalhes">
                            <span class="contato-nome">${outroNome}</span>
                            <span class="contato-ultima">${ultimaMsg}</span>
                        </div>
                        <span class="contato-data">${dataMsg}</span>
                    </div>
                `;

                div.addEventListener("click", () => {
                    window.location.href = `chat.html?chatId=${chat.id}`;
                });

                listaContatos.appendChild(div);
            }
        } catch (err) {
            console.error(err);
            listaContatos.innerHTML = `<p style="color:red;">${err.message}</p>`;
        }
    }

    carregarContatos();

    document.getElementById("btnVoltar").addEventListener("click", () => {
        window.location.href = "inicio.html";
    });
});

function logout() { localStorage.removeItem('token'); localStorage.removeItem('userNome'); window.location.href = 'login.html'; }