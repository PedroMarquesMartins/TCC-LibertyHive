const token = localStorage.getItem('token');
if (!token) {
    document.body.innerHTML = '<div style="padding:30px;text-align:center;"><h2>Você precisa estar logado.</h2></div>';
}

const painelEditarPerfil = document.getElementById('painelEditarPerfil');
const painelConta = document.getElementById('painelConta');
const painelMinhasPropostas = document.getElementById('painelMinhasPropostas');
const painelMinhaAvaliacao = document.getElementById('painelMinhaAvaliacao');
const painelNotificacoes = document.getElementById('painelNotificacoes');

function esconderTodosPainel() {
    document.querySelectorAll('.painel').forEach(p => p.style.display = 'none');
}

function getUserIdFromStorage() {
    const userId = localStorage.getItem('userId');
    if (!userId) {
        console.error("userId não encontrado no localStorage. É necessário fazer login novamente.");
        return null;
    }
    return parseInt(userId, 10);
}

function criarHTMLAvaliacao(media, quantidade) {
    if (quantidade === 0) {
        return `
            <div class="text-center text-muted">
                <i class="bi bi-star" style="font-size: 2rem;"></i>
                <p class="mt-2">Você ainda não possui avaliações.</p>
            </div>`;
    }

    let estrelasHTML = '';
    const notaArredondada = Math.round(media * 2) / 2;
    const fullStars = Math.floor(notaArredondada);
    const halfStar = (notaArredondada % 1 !== 0);
    const emptyStars = 5 - fullStars - (halfStar ? 1 : 0);

    for (let i = 0; i < fullStars; i++) estrelasHTML += '<i class="bi bi-star-fill"></i>';
    if (halfStar) estrelasHTML += '<i class="bi bi-star-half"></i>';
    for (let i = 0; i < emptyStars; i++) estrelasHTML += '<i class="bi bi-star"></i>';

    return `
        <div class="text-center">
            <div class="display-4" style="color: #ffc107;">${estrelasHTML}</div>
            <p class="h5 mt-2 mb-1">Sua média é ${media.toFixed(1)} de 5</p>
            <p class="text-muted">Baseado em ${quantidade} avaliações.</p>
        </div>`;
}

async function carregarMinhaAvaliacao() {
    const userId = getUserIdFromStorage();
    if (!userId) {
        Swal.fire('Erro de Sessão', 'Não foi possível encontrar seu ID de usuário. Por favor, faça login novamente.', 'error');
        return;
    }

    const container = document.getElementById('minhaAvaliacaoContainer');
    container.innerHTML = '<p>Carregando sua avaliação...</p>';

    try {
        const res = await fetch(`http://localhost:8080/api/propostas/avaliacoes/${userId}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (!res.ok) {
            const errorData = await res.json().catch(() => ({ message: 'Não foi possível buscar sua avaliação.' }));
            throw new Error(errorData.message);
        }

        const data = await res.json();
        container.innerHTML = criarHTMLAvaliacao(data.media, data.quantidade);

    } catch (err) {
        container.innerHTML = `<p class="text-danger">${err.message}</p>`;
        Swal.fire('Erro', err.message, 'error');
    }
}


async function carregarPerfil() {
    try {
        const userNome = localStorage.getItem('userNome');
        const res = await fetch('http://localhost:8080/api/escambista/porUserNome/' + userNome, {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!res.ok) throw new Error('Erro ao carregar perfil');
        const data = await res.json();
        document.getElementById('displayNomeReal').textContent = data.nomeEscambista || 'Nome Não Informado';
        document.getElementById('displayUserNome').textContent = `@${data.userNome || userNome}`;
        document.getElementById('editNomeReal').value = data.nomeEscambista || '';
        document.getElementById('editContato').value = data.contato || '';
        document.getElementById('editEndereco').value = data.endereco || '';
        document.getElementById('editCpf').value = data.cpf || '';
        document.getElementById('editDataNasc').value = data.dataNasc || '';
    } catch (err) {
        Swal.fire('Erro', err.message, 'error');
    }
}

async function carregarConta() {
    try {
        const userNome = localStorage.getItem('userNome');
        const res = await fetch('http://localhost:8080/api/cadastros/' + userNome, {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!res.ok) throw new Error('Erro ao carregar conta');
        const data = await res.json();
        document.getElementById('contaEmail').value = data.email || '';
        document.getElementById('contaUserNome').value = data.userNome || '';
    } catch (err) {
        Swal.fire('Erro', err.message, 'error');
    }
}

async function salvarPerfil() {
    try {
        const userNome = localStorage.getItem('userNome');
        const payload = {
            nomeEscambista: document.getElementById('editNomeReal').value,
            contato: document.getElementById('editContato').value,
            endereco: document.getElementById('editEndereco').value,
            cpf: document.getElementById('editCpf').value,
            dataNasc: document.getElementById('editDataNasc').value
        };
        const res = await fetch('http://localhost:8080/api/escambista/atualizarPorUserNome/' + userNome, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Erro ao salvar perfil');
        Swal.fire('Sucesso', 'Perfil atualizado!', 'success');
        carregarPerfil();
    } catch (err) {
        Swal.fire('Erro', err.message, 'error');
    }
}

async function salvarConta() {
    try {
        const userNomeOriginal = localStorage.getItem('userNome');
        const novoUserNome = document.getElementById('contaUserNome').value;
        const payload = {
            email: document.getElementById('contaEmail').value,
            userNome: novoUserNome,
            senha: document.getElementById('contaSenha').value
        };
        const res = await fetch('http://localhost:8080/api/cadastros/' + userNomeOriginal, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Erro ao salvar conta');
        Swal.fire('Sucesso', 'Conta atualizada!', 'success').then(() => {
            if (userNomeOriginal !== novoUserNome) {
                localStorage.setItem('userNome', novoUserNome);
                window.location.reload();
            }
        });
    } catch (err) {
        Swal.fire('Erro', err.message, 'error');
    }
}

async function carregarNotificacoes() {
    try {
        const userNome = localStorage.getItem('userNome');
        const res = await fetch(`http://localhost:8080/api/escambista/porUserNome/${userNome}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        const data = await res.json();
        document.getElementById('toggleNotificacoes').checked = data.querNotifi !== false;
    } catch (err) {
        Swal.fire('Erro', err.message, 'error');
    }
}

async function salvarNotificacoes() {
    try {
        const userNome = localStorage.getItem('userNome');
        const querNotifi = document.getElementById('toggleNotificacoes').checked;
        const res = await fetch(`http://localhost:8080/api/escambista/atualizarPorUserNome/${userNome}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ querNotifi: querNotifi })
        });
        if (!res.ok) throw new Error('Erro ao salvar notificações');
        Swal.fire('Sucesso', 'Preferências salvas!', 'success');
    } catch (err) {
        Swal.fire('Erro', err.message, 'error');
    }
}

document.getElementById('salvarPerfil').addEventListener('click', salvarPerfil);
document.getElementById('salvarConta').addEventListener('click', salvarConta);
document.getElementById('salvarNotificacoes').addEventListener('click', salvarNotificacoes);

function voltarInicio() {
    window.location.href = 'inicio.html';
}

function logout() {
    localStorage.clear();
    window.location.href = 'login.html';
}

document.addEventListener('DOMContentLoaded', () => {
    esconderTodosPainel();
    painelEditarPerfil.style.display = 'block';
    carregarPerfil();
});

document.getElementById('btnEditarPerfil').addEventListener('click', () => {
    esconderTodosPainel();
    painelEditarPerfil.style.display = 'block';
    carregarPerfil();
});

document.getElementById('btnConta').addEventListener('click', () => {
    esconderTodosPainel();
    painelConta.style.display = 'block';
    carregarConta();
});

document.getElementById('btnMinhasPropostas').addEventListener('click', () => {
    window.location.href = 'minhasPropostas.html';
});

document.getElementById('btnMinhaAvaliacao').addEventListener('click', () => {
    esconderTodosPainel();
    painelMinhaAvaliacao.style.display = 'block';
    carregarMinhaAvaliacao();
});

document.getElementById('btnNotificacoes').addEventListener('click', () => {
    esconderTodosPainel();
    painelNotificacoes.style.display = 'block';
    carregarNotificacoes();
});