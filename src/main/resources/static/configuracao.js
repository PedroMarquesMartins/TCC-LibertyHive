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
});

document.getElementById('btnNotificacoes').addEventListener('click', () => {
    esconderTodosPainel();
    painelNotificacoes.style.display = 'block';
    carregarNotificacoes();
});
