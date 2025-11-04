const token = localStorage.getItem('token');
let propostaIdParaAvaliar = null;
let idAvaliado = null;
let nomeAvaliado = null;
let notaSelecionada = 0;

if (!token) {
    document.body.innerHTML = '<div style="padding:30px;text-align:center;"><h2>Você precisa estar logado.</h2></div>';
}

document.getElementById('btnVoltar')?.addEventListener('click', () => {
    window.location.href = 'inicio.html';
});

function statusBadgeClass(status) {
    switch (status) {
        case 1: return ['Pendente', 'badge-status badge-pendente'];
        case 2: return ['Concluída', 'badge-status badge-concluida'];
        case 0: return ['Cancelada', 'badge-status badge-cancelada'];
        case 3: return ['Recusada', 'badge-status badge-recusada'];
        default: return ['Desconhecido', 'badge-status'];
    }
}

function getLoggedUser() {
    const userIdStr = localStorage.getItem('userId');
    const userNome = localStorage.getItem('userNome');
    const userId = userIdStr ? parseInt(userIdStr, 10) : null;
    if (!userId || !userNome) {
        console.error('Usuário não logado corretamente');
        return null;
    }
    return { userId, userNome };
}

function escapeHtml(str) {
    return (str || '').toString()
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

async function carregarPropostas() {
    try {
        const res = await fetch('http://localhost:8080/api/propostas/listarPropostas', {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!res.ok) throw new Error('Erro ao carregar propostas');
        const propostas = await res.json();
        renderPropostas(propostas);
    } catch (err) {
        console.error(err);
        document.getElementById('abertas').innerHTML =
            '<div class="no-propostas">Erro ao carregar propostas.</div>';
    }
}

function criarItemCardHTML(item) {
    const placeholderUrl = 'https://via.placeholder.com/400x300/CCCCCC/FFFFFF?text=Sem+Imagem';
    const imgSrc = item?.imagem ? `data:image/jpeg;base64,${item.imagem}` : placeholderUrl;
    return `
        <div>
            <img src="${imgSrc}" alt="${escapeHtml(item.nomePostagem)}">
            <h3>${escapeHtml(item.nomePostagem || '---')}</h3>
            ${item.userNome ? `<div class="small-muted">Dono: ${escapeHtml(item.userNome)}</div>` : ''}
            <div class="item-meta">${escapeHtml(item.categoria || '')} • ${escapeHtml(item.uf || '')}</div>
            <div class="small-muted">${escapeHtml(item.descricao || '')}</div>
        </div>
    `;
}

function renderPropostas(propostas) {
    const abertasEl = document.getElementById('abertas');
    const encerradasEl = document.getElementById('encerradas');
    abertasEl.innerHTML = '';
    encerradasEl.innerHTML = '';

    if (!propostas || propostas.length === 0) {
        abertasEl.innerHTML = '<div class="no-propostas">Você não tem propostas.</div>';
        return;
    }

    const abertas = propostas.filter(p => p.status === 1);
    const finalizadas = propostas.filter(p => p.status !== 1);

    function buildCard(p) {
        const [statusText, statusClass] = statusBadgeClass(p.status);
        const loggedUser = getLoggedUser();
        const quemPropôs = p.proponenteNome || `Usuário ${p.userId01 || ''}`;
        const quemRecebeu = p.receptorNome || `Usuário ${p.userId02 || ''}`;
        const infoQuem = (p.enviadoPeloUsuarioLogado)
            ? `Você propôs para ${escapeHtml(quemRecebeu)}`
            : `${escapeHtml(quemPropôs)} propôs para você`;

        const itemDesejadoHTML = criarItemCardHTML(p.itemDesejado || {});
        const itemOferecidoHTML = p.itemOferecido
            ? criarItemCardHTML(p.itemOferecido)
            : `<div><h3>Doação</h3><p class="small-muted">Sem item oferecido</p></div>`;

        const container = document.createElement('div');
        container.className = 'proposta-card';

        if (p.dataHora) {
            const dataEl = document.createElement('div');
            dataEl.className = 'meta data-hora-top';
            const dateObj = new Date(p.dataHora);
            const formattedDate = `${String(dateObj.getDate()).padStart(2, '0')}/${String(dateObj.getMonth() + 1).padStart(2, '0')}/${dateObj.getFullYear()} ${String(dateObj.getHours()).padStart(2, '0')}:${String(dateObj.getMinutes()).padStart(2, '0')}`;
            dataEl.textContent = `Enviada em: ${formattedDate}`;
            container.appendChild(dataEl);
        }

        const row = document.createElement('div');
        row.className = 'row-proposta';
        row.style.flex = '1';

        const divDesejado = document.createElement('div');
        divDesejado.className = 'item-card';
        divDesejado.innerHTML = itemDesejadoHTML;

        const divOferecido = document.createElement('div');
        divOferecido.className = 'item-card';
        divOferecido.innerHTML = itemOferecidoHTML;

        [divDesejado, divOferecido].forEach((div, index) => {
            const item = index === 0 ? p.itemDesejado : p.itemOferecido;
            const btnDetalhes = document.createElement("button");
            btnDetalhes.textContent = "VER DETALHES";
            btnDetalhes.className = "detalhes";
            btnDetalhes.onclick = () => {
                if (item?.id) {
                    window.location.href = `detalhesItem.html?id=${item.id}`;
                } else {
                    alert('ID do item não encontrado!');
                }
            };
            div.appendChild(btnDetalhes);
        });

        row.appendChild(divDesejado);
        row.appendChild(divOferecido);

        const actions = document.createElement('div');
        actions.className = 'acoes';
        const badge = document.createElement('div');
        badge.className = statusClass;
        badge.textContent = statusText;
        const quemEl = document.createElement('div');
        quemEl.className = 'meta';
        quemEl.textContent = infoQuem;
        actions.appendChild(badge);
        actions.appendChild(quemEl);

        if (p.status === 1 && loggedUser) {
            if (p.enviadoPeloUsuarioLogado) {
                const btnCancelar = document.createElement('button');
                btnCancelar.className = 'cancelar';
                btnCancelar.textContent = 'Cancelar';
                btnCancelar.onclick = async () => {
                    const confirm = await Swal.fire({
                        title: 'Deseja cancelar a proposta?',
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonText: 'Sim',
                        cancelButtonText: 'Não'
                    });
                    if (confirm.isConfirmed) {
                        await fetch(`http://localhost:8080/api/propostas/acao?propostaId=${p.idProposta}&acao=cancelar`, {
                            method: 'POST',
                            headers: { 'Authorization': 'Bearer ' + token }
                        });
                        carregarPropostas();
                    }
                };
                actions.appendChild(btnCancelar);
            } else {
                const btnAceitar = document.createElement('button');
                btnAceitar.className = 'aceitar';
                btnAceitar.textContent = 'Aceitar';
                btnAceitar.onclick = async () => {
                    const confirm = await Swal.fire({
                        title: 'Aceitar proposta?',
                        icon: 'question',
                        showCancelButton: true,
                        confirmButtonText: 'Sim',
                        cancelButtonText: 'Não'
                    });
                    if (!confirm.isConfirmed) return;

                    Swal.fire({
                        title: 'Aceitando proposta...',
                        html: 'Aguarde enquanto processamos.',
                        allowOutsideClick: false,
                        didOpen: () => Swal.showLoading()
                    });

                    try {
                        const resp = await fetch(`http://localhost:8080/api/propostas/acao?propostaId=${p.idProposta}&acao=concluir`, {
                            method: 'POST',
                            headers: { 'Authorization': 'Bearer ' + token }
                        });

                        const data = await resp.json().catch(() => ({}));
                        Swal.close();

                        if (resp.ok) {
                            Swal.fire({ title: 'Sucesso!', text: 'Proposta aceita!', icon: 'success' });
                            carregarPropostas();
                        } else {
                            Swal.fire({ title: 'Erro', text: data.message || 'Não foi possível aceitar a proposta.', icon: 'error' });
                        }
                    } catch (err) {
                        Swal.close();
                        console.error(err);
                        Swal.fire({ title: 'Erro', text: 'Falha ao conectar ao servidor.', icon: 'error' });
                    }
                };

                const btnRecusar = document.createElement('button');
                btnRecusar.className = 'recusar';
                btnRecusar.textContent = 'Recusar';
                btnRecusar.onclick = async () => {
                    const confirm = await Swal.fire({
                        title: 'Recusar proposta?',
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonText: 'Sim',
                        cancelButtonText: 'Não'
                    });
                    if (confirm.isConfirmed) {
                        await fetch(`http://localhost:8080/api/propostas/acao?propostaId=${p.idProposta}&acao=recusar`, {
                            method: 'POST',
                            headers: { 'Authorization': 'Bearer ' + token }
                        });
                        carregarPropostas();
                    }
                };
                actions.appendChild(btnAceitar);
                actions.appendChild(btnRecusar);
            }
        } else if (p.status === 2 && loggedUser) {
            const btnAvaliar = document.createElement('button');
            btnAvaliar.className = 'avaliar';
            btnAvaliar.textContent = 'Avaliar Perfil';
            btnAvaliar.onclick = () => abrirModalAvaliacao(p);
            actions.appendChild(btnAvaliar);
        }

        const btnChat = document.createElement('button');
        btnChat.className = 'chat';
        btnChat.textContent = 'Chat';
        btnChat.onclick = async () => {
            if (!loggedUser) return alert('Erro: usuário não identificado.');
            const outroId = p.enviadoPeloUsuarioLogado ? p.userId02 : p.userId01;
            const outroNome = p.enviadoPeloUsuarioLogado ? p.receptorNome : p.proponenteNome;
            const resp = await fetch(`http://localhost:8080/chat/criar?userId01=${loggedUser.userId}&userId02=${outroId}&userNome01=${encodeURIComponent(loggedUser.userNome)}&userNome02=${encodeURIComponent(outroNome)}`, {
                method: 'POST',
                headers: { 'Authorization': 'Bearer ' + token }
            });
            const data = await resp.json();
            if (resp.ok && data.id) {
                window.location.href = `chat.html?chatId=${data.id}`;
            } else alert('Erro ao abrir chat.');
        };
        actions.appendChild(btnChat);

        container.appendChild(row);
        container.appendChild(actions);
        return container;
    }

    abertas.forEach(p => abertasEl.appendChild(buildCard(p)));
    finalizadas.forEach(p => encerradasEl.appendChild(buildCard(p)));
}

function abrirModalAvaliacao(p) {
    const { userId: userLogado } = getLoggedUser();
    idAvaliado = (userLogado === p.userId01) ? p.userId02 : p.userId01;
    nomeAvaliado = (userLogado === p.userId01) ? p.receptorNome : p.proponenteNome;
    propostaIdParaAvaliar = p.idProposta;
    document.getElementById('avaliarNome').textContent = nomeAvaliado;
    atualizarEstrelas(0);
    notaSelecionada = 0;
    const modal = new bootstrap.Modal(document.getElementById('avaliacaoModal'));
    modal.show();
}

function atualizarEstrelas(nota) {
    document.querySelectorAll('#estrelas .star').forEach(star => {
        const valor = parseInt(star.dataset.value);
        star.style.color = valor <= nota ? '#f4b400' : '#ccc';
        star.classList.toggle('selecionada', valor <= nota);
    });
    const textos = ['Selecione uma nota', 'Péssimo 😠', 'Ruim 😕', 'Regular 😐', 'Bom 🙂', 'Excelente 😄'];
    document.getElementById('avaliacaoTexto').textContent = textos[nota] || textos[0];
}

document.querySelectorAll('#estrelas .star').forEach(star => {
    star.addEventListener('click', () => {
        notaSelecionada = parseInt(star.dataset.value);
        atualizarEstrelas(notaSelecionada);
    });
});

document.getElementById('btnEnviarAvaliacao')?.addEventListener('click', async () => {
    if (!notaSelecionada) {
        mostrarAviso('warning', 'Selecione uma nota antes de enviar.');
        return;
    }

    try {
        const resp = await fetch('http://localhost:8080/api/propostas/avaliar', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                propostaId: propostaIdParaAvaliar,
                nota: notaSelecionada,
                usuarioAvaliadoId: idAvaliado
            })
        });

        const data = await resp.json().catch(() => ({}));

        if (resp.ok) {
            mostrarAviso('success', data.message || 'Avaliação enviada com sucesso!');
        } else if (resp.status === 409) {
            mostrarAviso('warning', data.message || 'Esta proposta já foi avaliada.');
        } else {
            mostrarAviso('error', data.message || 'Erro ao enviar avaliação.');
        }
    } catch (err) {
        console.error('Erro ao enviar avaliação:', err);
        mostrarAviso('error', 'Falha ao conectar ao servidor.');
    }

    bootstrap.Modal.getInstance(document.getElementById('avaliacaoModal')).hide();
});

function mostrarAviso(tipo, mensagem) {
    const avisoEl = document.getElementById('avisoModal');
    const avisoModal = new bootstrap.Modal(avisoEl);
    const avisoTexto = document.getElementById('avisoTexto');
    const avisoIcon = document.getElementById('avisoIcon');
    const avisoHeader = avisoEl.querySelector('.modal-header');
    avisoTexto.textContent = mensagem;
    avisoHeader.className = 'modal-header text-white';
    switch (tipo) {
        case 'success':
            avisoHeader.classList.add('bg-success');
            avisoIcon.className = 'bi bi-check-circle fs-1 mb-3 text-success';
            break;
        case 'warning':
            avisoHeader.classList.add('bg-warning');
            avisoIcon.className = 'bi bi-exclamation-triangle fs-1 mb-3 text-warning';
            break;
        default:
            avisoHeader.classList.add('bg-danger');
            avisoIcon.className = 'bi bi-x-circle fs-1 mb-3 text-danger';
    }
    avisoModal.show();
}

carregarPropostas();
