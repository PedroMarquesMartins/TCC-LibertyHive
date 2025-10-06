const token = localStorage.getItem('token');
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
        console.error('Usuário não logado corretamente (token inválido ou userId não definido)');
        return null;
    }
    return { userId, userNome };
}

function getLoggedUserFromStorageOrToken() {
    const possibleIdKeys = ['userId', 'userID', 'id'];
    const possibleNameKeys = ['userNome', 'userName', 'username', 'nome'];

    let id = null;
    for (const k of possibleIdKeys) {
        const v = localStorage.getItem(k);
        if (v !== null && v !== undefined && v !== '') {
            const n = Number(v);
            if (!isNaN(n)) { id = n; break; }
        }
    }
    let nome = null;
    for (const k of possibleNameKeys) {
        const v = localStorage.getItem(k);
        if (v) { nome = v; break; }
    }
    if (id) return { id, nome };

    if (token && token.split && token.split('.').length === 3) {
        try {
            const payload = token.split('.')[1];
            let b64 = payload.replace(/-/g, '+').replace(/_/g, '/');
            while (b64.length % 4) b64 += '=';
            const jsonStr = atob(b64);
            const obj = JSON.parse(jsonStr);
            const possibleIdFields = ['id', 'userId', 'user_id', 'sub', 'userid'];
            const possibleNameFields = ['name', 'nome', 'preferred_username', 'username', 'user_nome'];
            for (const f of possibleIdFields) {
                if (obj[f] !== undefined && obj[f] !== null) {
                    const n = Number(obj[f]);
                    if (!isNaN(n)) id = n;
                }
            }
            for (const f of possibleNameFields) {
                if (!nome && obj[f]) nome = obj[f];
            }
            if (id) {
                localStorage.setItem('userId', String(id));
                if (nome) localStorage.setItem('userNome', String(nome));
                return { id, nome };
            }
        } catch (err) {
            console.debug('não foi possível decodificar token como JWT', err);
        }
    }

    return null;
}

async function carregarPropostas() {
    try {
        const res = await fetch('http://localhost:8080/api/propostas/listarPropostas', {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!res.ok) {
            const txt = await res.text().catch(() => '');
            console.error('Erro listarPropostas:', res.status, txt);
            throw new Error('Erro ao carregar propostas');
        }
        const propostas = await res.json();
        renderPropostas(propostas);
    } catch (err) {
        console.error(err);
        document.getElementById('abertas').innerHTML = '<div class="no-propostas">Erro ao carregar propostas.</div>';
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

function escapeHtml(str) {
    return (str || '').toString()
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function renderPropostas(propostas) {
    const abertasEl = document.getElementById('abertas');
    const encerradasEl = document.getElementById('encerradas');
    if (!abertasEl || !encerradasEl) return;
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
        const quemPropôs = p.proponenteNome || `Usuário ${p.userId01 || ''}`;
        const quemRecebeu = p.receptorNome || `Usuário ${p.userId02 || ''}`;
        const infoQuem = (p.enviadoPeloUsuarioLogado)
            ? `Você propôs para ${escapeHtml(quemRecebeu)}`
            : `${escapeHtml(quemPropôs)} propôs para você`;

        const itemDesejadoHTML = criarItemCardHTML(p.itemDesejado || {});
        const itemOferecidoHTML = p.itemOferecido ? criarItemCardHTML(p.itemOferecido) : `<div><h3>Doação</h3><p class="small-muted">Sem item oferecido</p></div>`;

        const container = document.createElement('div');
        container.className = 'proposta-card';

        const row = document.createElement('div');
        row.className = 'row-proposta';
        row.style.flex = '1';

        const divDesejado = document.createElement('div');
        divDesejado.className = 'item-card';
        divDesejado.innerHTML = itemDesejadoHTML;

        const divOferecido = document.createElement('div');
        divOferecido.className = 'item-card';
        divOferecido.innerHTML = itemOferecidoHTML;

        [divDesejado, divOferecido].forEach(div => {
            const btnSim = document.createElement("button");
            btnSim.textContent = "VER DETALHES";
            btnSim.className = "detalhes";
            btnSim.onclick = () => {
                Swal.fire({
                    title: div.querySelector('h3').textContent,
                    html: `
                        <p><strong>Usuário:</strong> ${escapeHtml(div.querySelector('.small-muted')?.textContent.replace('Dono: ', '') || '---')}</p>
                        <p><strong>Categoria:</strong> ${escapeHtml(div.querySelector('.item-meta')?.textContent.split('•')[0].trim() || '')}</p>
                        <p><strong>Cidade/UF:</strong> ${escapeHtml(div.querySelector('.item-meta')?.textContent.split('•')[1]?.trim() || '')}</p>
                        <p><strong>Doação:</strong> ${div.querySelector('h3').textContent === 'Doação' ? 'Sim' : 'Não'}</p>
                    `,
                    imageUrl: div.querySelector('img')?.src || null,
                    imageAlt: div.querySelector('h3')?.textContent || ''
                });
            };
            div.appendChild(btnSim);
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

        if (p.status === 1 && p.idProposta && !isNaN(p.idProposta)) {
            if (p.podeCancelar) {
                const btn = document.createElement('button');
                btn.className = 'cancelar';
                btn.textContent = 'Cancelar';
                btn.onclick = () => confirmarAcao(p.idProposta, 'cancelar');
                actions.appendChild(btn);
            }
            if (p.podeConcluir) {
                const btn = document.createElement('button');
                btn.className = 'concluir';
                btn.textContent = 'Concluir';
                btn.onclick = () => confirmarAcao(p.idProposta, 'concluir');
                actions.appendChild(btn);
            }
            if (p.podeRecusar) {
                const btn = document.createElement('button');
                btn.className = 'recusar';
                btn.textContent = 'Recusar';
                btn.onclick = () => confirmarAcao(p.idProposta, 'recusar');
                actions.appendChild(btn);
            }
            const btnChat = document.createElement('button');
            btnChat.className = 'chat';
            btnChat.textContent = 'Chat';
            btnChat.onclick = async () => {
                const loggedUser = getLoggedUser();
                if (!loggedUser) {
                    alert('Erro: usuário não identificado corretamente.');
                    return;
                }

                const { userId: userIdLogado, userNome: userNomeLogado } = loggedUser;

                const outroId = p.enviadoPeloUsuarioLogado ? p.userId02 : p.userId01;
                const outroNome = p.enviadoPeloUsuarioLogado ? p.receptorNome : p.proponenteNome;

                if (!outroId || !outroNome) {
                    alert('Erro: usuário alvo do chat não identificado.');
                    return;
                }

                try {
                    const response = await fetch(`http://localhost:8080/chat/criar?userId01=${userIdLogado}&userId02=${outroId}&userNome01=${encodeURIComponent(userNomeLogado)}&userNome02=${encodeURIComponent(outroNome)}`, {
                        method: 'POST',
                        headers: { 'Authorization': 'Bearer ' + token }
                    });

                    const data = await response.json();

                    if (response.ok && data.id) {
                        window.location.href = `chat.html?chatId=${data.id}`;
                    } else {
                        console.error('Erro resposta servidor:', data);
                        alert('Erro ao criar ou carregar chat.');
                    }
                } catch (err) {
                    console.error('Erro ao tentar criar chat:', err);
                    alert('Falha na conexão com o servidor.');
                }
            };
            actions.appendChild(btnChat);
        }

        container.appendChild(row);
        container.appendChild(actions);

        return container;
    }

    if (abertas.length === 0) {
        abertasEl.innerHTML = '<div class="no-propostas">Nenhuma proposta pendente.</div>';
    } else {
        abertas.forEach(p => abertasEl.appendChild(buildCard(p)));
    }

    if (finalizadas.length === 0) {
        encerradasEl.innerHTML = '<div class="no-propostas">Nenhuma proposta encerrada.</div>';
    } else {
        finalizadas.forEach(p => encerradasEl.appendChild(buildCard(p)));
    }
}

async function confirmarAcao(propostaId, acao) {
    const labels = { cancelar: 'cancelar', concluir: 'concluir', recusar: 'recusar' };
    const confirm = await Swal.fire({
        title: `Confirma ${labels[acao]}?`,
        text: `Deseja realmente ${labels[acao]} esta proposta?`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Sim',
        cancelButtonText: 'Cancelar'
    });
    if (!confirm.isConfirmed) return;

    try {
        const res = await fetch(`http://localhost:8080/api/propostas/acao?propostaId=${propostaId}&acao=${acao}`, {
            method: 'POST',
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!res.ok) {
            const txt = await res.text().catch(() => '');
            console.error('Erro atualizarStatus', res.status, txt);
            throw new Error('Erro ao atualizar proposta');
        }
        await carregarPropostas();
    } catch (err) {
        console.error(err);
        Swal.fire({ title: 'Erro', text: 'Não foi possível atualizar a proposta.', icon: 'error' });
    }
}

carregarPropostas();
