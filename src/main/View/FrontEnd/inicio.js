document.addEventListener('DOMContentLoaded', function () {
    const nome = localStorage.getItem('userNome');

    if (!nome) {
        window.location.href = 'login.html';
        return;
    }

    document.getElementById('usuarioNome').textContent = nome;
    const produtos = [
        { nome: "Produto 1", descricao: "Descrição do Produto 1", img: "./imagens/g.png" },
        { nome: "Produto 2", descricao: "Descrição do Produto 2", img: "./imagens/g.png" },
        { nome: "Serviço 1", descricao: "Descrição do Serviço 1", img: "./imagens/v.png" }
    ];

    const lista = document.getElementById('listaProdutos');
    produtos.forEach(p => {
        const card = document.createElement('div');
        card.className = 'product-card';
        card.innerHTML = `
                    <img src="${p.img}" alt="${p.nome}">
                    <h3>${p.nome}</h3>
                    <p>${p.descricao}</p>
                `;
        lista.appendChild(card);
    });
});

function verMeusItens() {
    window.location.href = 'meusItens.html';
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userNome');
    window.location.href = 'login.html';
}

function filtrarProdutos() {
    const filtro = document.getElementById('search').value.toLowerCase();
    const cards = document.querySelectorAll('.product-card');
    cards.forEach(card => {
        const nome = card.querySelector('h3').textContent.toLowerCase();
        const descricao = card.querySelector('p').textContent.toLowerCase();
        card.style.display = (nome.includes(filtro) || descricao.includes(filtro)) ? '' : 'none';
    });
}