const token = localStorage.getItem('token');
if (!token) {
    document.body.innerHTML = '<div style="padding:30px;text-align:center;"><h2>Você precisa estar logado.</h2></div>';
}
//Elementos dos painéis
const painelEditarPerfil = document.getElementById('painelEditarPerfil');
const painelConta = document.getElementById('painelConta');
const painelMinhasPropostas = document.getElementById('painelMinhasPropostas');
const painelMinhaAvaliacao = document.getElementById('painelMinhaAvaliacao');
const painelNotificacoes = document.getElementById('painelNotificacoes');

//Função para esconder todos os painéis
function esconderTodosPainel() {
    document.querySelectorAll('.painel').forEach(p => p.style.display = 'none');
}
//Função para obter o userId do localStorage
function getUserIdFromStorage() {
    const userId = localStorage.getItem('userId');
    if (!userId) {
        console.error("userId não encontrado no localStorage. É necessário fazer login novamente.");
        return null;
    }
    return parseInt(userId, 10);
}
//Função para criar o HTML da avaliação
function criarHTMLAvaliacao(media, quantidade) {
    if (quantidade === 0) {
        return `
            <div class="text-center text-muted">
                <i class="bi bi-star" style="font-size: 2rem;"></i>
                <p class="mt-2">Você ainda não possui avaliações.</p>
            </div>`;
    }
        //Gerar estrelas com base na média
    let estrelasHTML = '';
    const notaArredondada = Math.round(media * 2) / 2;
    const fullStars = Math.floor(notaArredondada);
    const halfStar = (notaArredondada % 1 !== 0);
    const emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
//Montar o HTML das estrelas
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
//Função para carregar a avaliação do usuário
async function carregarMinhaAvaliacao() {
    const userId = getUserIdFromStorage();
    if (!userId) {
        Swal.fire('Erro de Sessão', 'Não foi possível encontrar seu ID de usuário. Por favor, faça login novamente.', 'error');
        return;
    }
    
    //Carregar avaliação do backend

    const container = document.getElementById('minhaAvaliacaoContainer');
    container.innerHTML = '<p>Carregando sua avaliação...</p>';

    try {//Requisição para o backend
        const res = await fetch(`http://localhost:8080/api/propostas/avaliacoes/${userId}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (!res.ok) {
            const errorData = await res.json().catch(() => ({ message: 'Não foi possível buscar sua avaliação.' }));
            throw new Error(errorData.message);
        }//Processar os dados da resposta

        const data = await res.json();
        container.innerHTML = criarHTMLAvaliacao(data.media, data.quantidade);

    } catch (err) {
        container.innerHTML = `<p class="text-danger">${err.message}</p>`;
        Swal.fire('Erro', err.message, 'error');
    }
}

//Função para carregar os dados do perfil do usuário
async function carregarPerfil() {
    try {
        const userNome = localStorage.getItem('userNome');
        const res = await fetch('http://localhost:8080/api/escambista/porUserNome/' + userNome, {
            headers: { 'Authorization': 'Bearer ' + token }
        });//Requisição para o backend
        if (!res.ok) throw new Error('Erro ao carregar perfil');
        const data = await res.json();//Preencher os campos com os dados retornados
        document.getElementById('displayNomeReal').textContent = data.nomeEscambista || 'Nome Não Informado';
        document.getElementById('displayUserNome').textContent = `@${data.userNome || userNome}`;
        document.getElementById('editNomeReal').value = data.nomeEscambista || '';
        document.getElementById('editContato').value = data.contato || '';
        document.getElementById('editEndereco').value = data.endereco || '';
        document.getElementById('editCpf').value = data.cpf || '';
        document.getElementById('editDataNasc').value = data.dataNasc || '';
    } catch (err) {
    }
}


//Função para carregar os dados da conta do usuário
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
    }
}

// Salvar as alterações do perfil
async function salvarPerfil() {
    try {
        const cpf = document.getElementById('editCpf').value.trim();
        if (!validarCpfFront(cpf)) {
            Swal.fire('Erro', 'CPF inválido. Verifique os dígitos e o formato.', 'error');
            return;
        }
//Preparar os dados para envio
        const userNome = localStorage.getItem('userNome');
        const payload = {
            nomeEscambista: document.getElementById('editNomeReal').value,
            contato: document.getElementById('editContato').value,
            endereco: document.getElementById('editEndereco').value,
            cpf: cpf,
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
        if (!res.ok) {
            const errorData = await res.json().catch(() => ({ message: 'Erro ao salvar perfil' }));
            throw new Error(errorData.message);
        }
        Swal.fire('Sucesso', 'Perfil atualizado!', 'success');
        carregarPerfil();
    } catch (err) {
        Swal.fire('Erro', err.message, 'error');
    }
}

//Validação de CPF
function validarCpfFront(cpf) {
    if (!cpf) return false;
    let s = cpf.replace(/\D/g, '');
    if (s.length !== 11 || /^(\d)\1{10}$/.test(s)) return false;
    let sum, r;
    sum = 0;
    for (let i = 0; i < 9; i++) sum += parseInt(s[i]) * (10 - i);
    r = 11 - (sum % 11);
    let d1 = (r === 10 || r === 11) ? 0 : r;
    if (d1 !== parseInt(s[9])) return false;

    sum = 0;
    for (let i = 0; i < 10; i++) sum += parseInt(s[i]) * (11 - i);
    r = 11 - (sum % 11);
    let d2 = (r === 10 || r === 11) ? 0 : r;
    return d2 === parseInt(s[10]);
}
//Máscara de CPF
const inputCpf = document.getElementById('editCpf');
if (inputCpf) {
    function formatarCpf(valor) {
        const nums = valor.replace(/\D/g, '').slice(0, 11);
        if (nums.length <= 3) return nums;
        if (nums.length <= 6) return nums.replace(/(\d{3})(\d+)/, '$1.$2');
        if (nums.length <= 9) return nums.replace(/(\d{3})(\d{3})(\d+)/, '$1.$2.$3');
        return nums.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, '$1.$2.$3-$4');
    }
    inputCpf.addEventListener('input', (e) => {
        const pos = e.target.selectionStart;
        const rawBefore = e.target.value;
        const formatted = formatarCpf(rawBefore);
        e.target.value = formatted;
        const nonDigitsBefore = (rawBefore.slice(0, pos).match(/\D/g) || []).length;
        const newPos = Math.max(0, pos - nonDigitsBefore);
        e.target.selectionStart = e.target.selectionEnd = Math.min(e.target.value.length, newPos + Math.floor(e.target.value.length / 4));
    });

    inputCpf.addEventListener('paste', (e) => {
        e.preventDefault();
        const clipboard = (e.clipboardData || window.clipboardData).getData('text') || '';
        const onlyNums = clipboard.replace(/\D/g, '').slice(0, 11);
        inputCpf.value = formatarCpf(onlyNums);
    });

    inputCpf.addEventListener('keypress', (e) => {
        const char = String.fromCharCode(e.charCode || e.keyCode);
        if (!/[0-9]/.test(char)) e.preventDefault();
    });
}
//Máscara de telefone
const inputContato = document.getElementById('editContato');
if (inputContato) {
    function formatarTelefone(valor) {
        let nums = valor.replace(/\D/g, '').slice(0, 11);
        if (nums.length === 0) return '';
        if (nums.length <= 2) return `(${nums}`;
        if (nums.length <= 6) return `(${nums.slice(0, 2)}) ${nums.slice(2)}`;
        if (nums.length <= 10) {
            return `(${nums.slice(0, 2)}) ${nums.slice(2, 6)}-${nums.slice(6)}`;
        }
        else {
            return `(${nums.slice(0, 2)}) ${nums.slice(2, 7)}-${nums.slice(7)}`;
        }
    }

    inputContato.addEventListener('input', (e) => {
        const pos = e.target.selectionStart;
        const rawValue = e.target.value;
        const formatted = formatarTelefone(rawValue);
        const digitsBefore = (rawValue.slice(0, pos).match(/\d/g) || []).length;
        e.target.value = formatted;
        let newPos = 0;
        let digitsCounted = 0;
        while (newPos < formatted.length && digitsCounted < digitsBefore) {
            if (/\d/.test(formatted[newPos])) {
                digitsCounted++;
            }
            newPos++;
        }
        while (newPos < formatted.length && /\D/.test(formatted[newPos])) {
            newPos++;
        }
        if (rawValue.length > formatted.length) {
            e.target.selectionStart = e.target.selectionEnd = pos;
        } else {
            e.target.selectionStart = e.target.selectionEnd = newPos;
        }
    });

    inputContato.addEventListener('paste', (e) => {
        e.preventDefault();
        const clipboard = (e.clipboardData || window.clipboardData).getData('text') || '';
        const onlyNums = clipboard.replace(/\D/g, '').slice(0, 11);
        inputContato.value = formatarTelefone(onlyNums);
    });

    inputContato.addEventListener('keypress', (e) => {
        const char = String.fromCharCode(e.charCode || e.keyCode);
        if (!/[0-9]/.test(char)) e.preventDefault();
    });
}

// Salvar as alterações da conta e senha
async function salvarConta() {
    try {
        const userNomeOriginal = localStorage.getItem('userNome');
        const token = localStorage.getItem('token'); // ✅ garante que o token é obtido
        const novoUserNome = document.getElementById('contaUserNome').value;
        const payload = {
            email: document.getElementById('contaEmail').value,
            userNome: novoUserNome,
            senha: document.getElementById('contaSenha').value
        };
//Enviar os dados para o backend
        const res = await fetch('http://localhost:8080/api/cadastros/' + userNomeOriginal, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            const errData = await res.json().catch(() => ({}));
            throw new Error(errData.message || 'Erro ao salvar conta');
        }

        const response = await res.json();

        Swal.fire('Sucesso', 'Conta atualizada!', 'success').then(() => {
            if (response.token) {
                localStorage.setItem('token', response.token);
            }
            localStorage.setItem('userNome', novoUserNome);

            window.location.reload();
        });

    } catch (err) {
        Swal.fire('Erro', err.message, 'error');
        console.error('Erro ao atualizar conta:', err);
    }
}

//Funções para carregar e salvar preferências de notificações
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


//Eventos dos botões
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
//Inicialização da página
document.addEventListener('DOMContentLoaded', () => {
    esconderTodosPainel();
    painelEditarPerfil.style.display = 'block';
    carregarPerfil();
});

//Criação dos eventos dos botões laterais

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

document.getElementById('btnCategorias').addEventListener('click', () => {
    esconderTodosPainel();
    painelCategorias.style.display = 'block';
    carregarGraficoCategorias();
});
    //Gráfico de categorias das postagens
const painelCategorias = document.getElementById('painelCategorias');
let instanciaChartCategorias = null;

function gerarCoresPara(n) {
    const cores = [];
    for (let i = 0; i < n; i++) {
        const hue = Math.round((i * 360) / n);
        cores.push(`hsl(${hue} 70% 50%)`);
    }
    return cores;
}
//Função para buscar postagens do usuário de múltiplos endpoints
async function fetchPostagensTodasDoUsuario(token) {
    const endpoints = [
        'http://localhost:8080/api/postagens/listar-categorias-usuario'
    ];

    for (const url of endpoints) {
        try {
            const res = await fetch(url, {
                method: 'GET',
                headers: { 'Authorization': 'Bearer ' + token }
            });

            if (!res.ok) {
                console.warn(`Falha ao acessar ${url}: ${res.status}`);
                continue;
            }

            const data = await res.json();

            if (Array.isArray(data)) {
                return { data, usedUrl: url };
            } else {
                console.warn(`Resposta inesperada de ${url}`, data);
            }

        } catch (e) {
            console.error(`Erro ao tentar ${url}:`, e);
            continue;
        }
    }
    throw new Error('Não foi possível obter suas postagens (verifique o backend e a autenticação).');
}

//Ccontar categorias para o gráfico
function contarCategorias(postagens) {
    const contagem = {};
    postagens.forEach(p => {
        const cat = (p.categoria || 'Sem Categoria').trim() || 'Sem Categoria';
        contagem[cat] = (contagem[cat] || 0) + 1;
    });
    return contagem;
}

function montarLegendaHTML(labels, counts, colors) {
    let html = '';
    for (let i = 0; i < labels.length; i++) {
        html += `<div style="display:inline-block;margin:4px 8px;">
                    <span style="display:inline-block;width:12px;height:12px;background:${colors[i]};border-radius:3px;margin-right:6px;vertical-align:middle;"></span>
                    <span>${labels[i]} — ${counts[i]}</span>
                 </div>`;
    }
    return html;
}

async function carregarGraficoCategorias() {
    const token = localStorage.getItem('token');
    if (!token) {
        Swal.fire('Erro', 'Token não encontrado. Faça login novamente.', 'error');
        return;
    }
    const canvas = document.getElementById('chartCategorias');
    const legendaDiv = document.getElementById('legendaCategorias');

    if (canvas && canvas.getContext) {
        const ctx = canvas.getContext('2d');
        ctx.clearRect(0, 0, canvas.width, canvas.height);
    }
    legendaDiv.innerHTML = '<p>Carregando...</p>';

    try {
        const { data: postagens, usedUrl } = await fetchPostagensTodasDoUsuario(token);
        const contagem = contarCategorias(postagens);
        const labels = Object.keys(contagem).sort((a, b) => contagem[b] - contagem[a]);
        const counts = labels.map(l => contagem[l]);

        if (labels.length === 0) {
            legendaDiv.innerHTML = '<p class="text-muted">Você ainda não tem postagens.</p>';
            if (instanciaChartCategorias) {
                instanciaChartCategorias.destroy();
                instanciaChartCategorias = null;
            }
            return;
        }
        const colors = gerarCoresPara(labels.length);
        if (instanciaChartCategorias) {
            instanciaChartCategorias.data.labels = labels;
            instanciaChartCategorias.data.datasets[0].data = counts;
            instanciaChartCategorias.data.datasets[0].backgroundColor = colors;
            instanciaChartCategorias.update();
        } else {
            instanciaChartCategorias = new Chart(document.getElementById('chartCategorias').getContext('2d'), {
                type: 'pie',
                data: {
                    labels: labels,
                    datasets: [{
                        data: counts,
                        backgroundColor: colors,
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    }
                }
            });
        }

        legendaDiv.innerHTML = montarLegendaHTML(labels, counts, colors);
        if (usedUrl && usedUrl.endsWith('/listar')) {
            legendaDiv.innerHTML += '<div class="text-muted small mt-2">Observação: este backend está retornando apenas postagens disponíveis. Para contar postagens indisponíveis também, adicione o endpoint /listar-todas-usuario no backend.</div>';
        }

    } catch (err) {
        legendaDiv.innerHTML = `<p class="text-danger">${err.message}</p>`;
        Swal.fire('Erro', err.message, 'error');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const btnExcluir = document.getElementById('btnExcluirConta');
//Evento do botão de excluir conta
    if (btnExcluir) {
        btnExcluir.addEventListener('click', async () => {

            const confirmacaoResult = await Swal.fire({
                title: 'Tem certeza?',
                text: "Esta ação é permanente e não pode ser desfeita.",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Sim, excluir minha conta!',
                cancelButtonText: 'Cancelar'
            });

            if (!confirmacaoResult.isConfirmed) {
                return;
            }
    //Solicitar confirmação de senha
            const { value: senha } = await Swal.fire({
                title: 'Confirme sua senha',
                input: 'password',
                inputLabel: 'Para excluir sua conta, digite sua senha:',
                inputPlaceholder: 'Digite sua senha',
                inputAttributes: {
                    autocapitalize: 'off',
                    autocorrect: 'off'
                },
                showCancelButton: true,
                confirmButtonText: 'Confirmar Exclusão',
                cancelButtonText: 'Cancelar',
                showLoaderOnConfirm: true,
                preConfirm: (pass) => {
                    if (!pass || pass.trim() === "") {
                        Swal.showValidationMessage(`Senha é obrigatória.`);
                    }
                    return pass;
                },
                allowOutsideClick: () => !Swal.isLoading()
            });

            if (!senha) {
                return;
            }
//Enviar requisição para excluir a conta
            const userId = getUserIdFromStorage();
            const token = localStorage.getItem('token');

            if (!userId || !token) {
                Swal.fire('Erro de Sessão', 'Usuário não autenticado. Faça login novamente.', 'error');
                return;
            }

            try {
                const response = await fetch(`http://localhost:8080/api/escambista/excluir/${userId}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + token
                    },
                    body: JSON.stringify({ senha: senha })
                });

                const data = await response.json();

                if (response.ok) {
                    await Swal.fire(
                        'Excluída!',
                        data.message,
                        'success'
                    );
//Limpar localStorage e redireciona para login
                    localStorage.clear();
                    sessionStorage.clear();
                    window.location.href = 'login.html';
                } else {
                    Swal.fire(
                        'Erro!',
                        data.message,
                        'error'
                    );
                }

            } catch (error) {
                console.error("Erro ao tentar excluir conta:", error);
                Swal.fire(
                    'Erro de Conexão',
                    'Ocorreu um erro de conexão. Tente novamente.',
                    'error'
                );
            }
        });
    }
    //Inicialização da página
    esconderTodosPainel();
    if (painelEditarPerfil) {
        painelEditarPerfil.style.display = 'block';
        carregarPerfil();
    }
});