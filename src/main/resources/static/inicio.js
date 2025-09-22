document.addEventListener('DOMContentLoaded', async function () {
    const nome = localStorage.getItem('userNome');
    document.getElementById('usuarioNome').textContent = nome || "visitante";

    const lista = document.getElementById('listaProdutos');
    lista.innerHTML = "<p class='text-muted'>Carregando postagens...</p>";

    try {
        const token = localStorage.getItem("token");
        if (!token) {
            lista.innerHTML = "<p class='text-danger'>Você precisa estar logado para ver as postagens.</p>";
            return;
        }

        const response = await fetch("http://localhost:8080/api/postagens/listar-todas", {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) {
            lista.innerHTML = "<p class='text-danger'>Erro ao carregar postagens.</p>";
            return;
        }

        const postagens = await response.json();
        lista.innerHTML = "";

        if (!postagens || postagens.length === 0) {
            lista.innerHTML = "<p class='text-muted'>Nenhuma postagem encontrada.</p>";
            return;
        }

        const frag = document.createDocumentFragment();

        postagens.forEach(p => {
            const card = document.createElement('div');
            card.className = 'product-card';

            const cat = (p.categoria || '').toString();
            const nomePost = (p.nomePostagem || '').toString();

            card.dataset.id = p.id;
            card.dataset.categoria = normalizarTexto(cat);
            card.dataset.nome = normalizarTexto(nomePost);
            card.dataset.uf = (p.uf || '').toLowerCase();
            card.dataset.tipo = (p.isProdOuServico ? 'produto' : 'serviço');

            let imgSrc = "./imagens/placeholder.png";
            if (p.imagem) imgSrc = `data:image/jpeg;base64,${p.imagem}`;

            const doacao_string = p.doacao ? "Sim" : "Não";
            const tipo_string = p.isProdOuServico ? "Produto" : "Serviço";

            card.innerHTML = `
                <img src="${imgSrc}" alt="${escapeHtml(nomePost)}">
                <h3>${escapeHtml(nomePost)}</h3>
                <p><strong>Categoria:</strong> ${escapeHtml(cat)}</p>
                <p><strong>Tipo:</strong> ${tipo_string}</p>
                <p><strong>Doação:</strong> ${doacao_string}</p>
                <small>${escapeHtml(p.cidade || "")} - ${escapeHtml(p.uf || "")}</small>
                <div class="d-flex gap-2 mt-2">
                    <button class="btn btn-outline-primary btn-sm ver-detalhes-btn">Ver Detalhes</button>
                </div>
            `;
            card.querySelector('.ver-detalhes-btn').addEventListener('click', () => {
                window.location.href = `detalhesItem.html?id=${p.id}`;
            });

            frag.appendChild(card);
        });

        lista.appendChild(frag);

        adicionarBotaoFavoritos();

        lista.addEventListener('keydown', (e) => {
            if (e.key === 'ArrowRight') { scrollLista(1); }
            if (e.key === 'ArrowLeft') { scrollLista(-1); }
        });

    } catch (error) {
        console.error("Erro ao carregar postagens:", error);
        lista.innerHTML = "<p class='text-danger'>Erro de conexão com o servidor.</p>";
    }
});

function verMeusItens() { window.location.href = 'meusItens.html'; }

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userNome');
    window.location.href = 'login.html';
}

function filtrarProdutos() { aplicarFiltros(); }

function limparFiltros() {
    document.getElementById('search').value = '';
    document.getElementById('categoria').value = 'all';
    document.getElementById('uf').value = 'all';
    document.getElementById('tipo').value = 'all';
    aplicarFiltros();
}

function normalizarTexto(texto) {
    return (texto || '')
        .toString()
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "");
}

function aplicarFiltros() {
    const termo = normalizarTexto(document.getElementById('search').value);
    const categoriaSelecionada = normalizarTexto(document.getElementById('categoria').value);
    const ufSelecionada = document.getElementById('uf').value.toLowerCase();
    const tipoSelecionado = document.getElementById('tipo').value.toLowerCase();

    const cards = document.querySelectorAll('.product-card');

    cards.forEach(card => {
        const nom = card.dataset.nome || '';
        const cat = card.dataset.categoria || '';
        const cardUf = card.dataset.uf || '';
        const cardTipo = card.dataset.tipo || '';

        const bateBusca = !termo || nom.includes(termo) || cat.includes(termo);
        const bateCat = (categoriaSelecionada === 'all') || (cat === categoriaSelecionada);
        const bateUf = (ufSelecionada === 'all') || (cardUf === ufSelecionada);
        const bateTipo = (tipoSelecionado === 'all') || (cardTipo === tipoSelecionado);

        card.style.display = (bateBusca && bateCat && bateUf && bateTipo) ? '' : 'none';
    });
}

function scrollLista(direcao) {
    const lista = document.getElementById('listaProdutos');
    const passo = Math.max(320, Math.floor(lista.clientWidth * 0.9));
    lista.scrollBy({ left: direcao * passo, behavior: 'smooth' });
}

function escapeHtml(str) {
    return (str || '').toString()
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function adicionarBotaoFavoritos() {
    document.querySelectorAll(".product-card").forEach(card => {
        if (!card.querySelector(".fav-btn")) {
            const btn = document.createElement("button");
            btn.className = "btn btn-outline-danger btn-sm mt-2 fav-btn";
            btn.innerHTML = `<i class="bi bi-heart"></i> Favoritar`;

            btn.addEventListener("click", async () => {
                const postagemId = card.dataset.id;

                const token = localStorage.getItem("token");
                if (!token) {
                    Swal.fire({
                        title: 'Erro',
                        text: 'Você precisa estar logado para favoritar.',
                        icon: 'error'
                    });
                    return;
                }

                try {
                    const response = await fetch("http://localhost:8080/favoritos?postagemId=" + postagemId, {
                        method: "POST",
                        headers: { "Authorization": `Bearer ${token}` }
                    });

                    const data = await response.json();

                    if (response.ok) {
                        Swal.fire({
                            title: 'Item favoritado!',
                            text: data.message,
                            icon: 'success',
                            timer: 2000,
                            showConfirmButton: false
                        });
                    } else if (response.status === 409) {
                        Swal.fire({
                            title: 'Já favoritado',
                            text: data.message,
                            icon: 'info',
                            timer: 2000,
                            showConfirmButton: false
                        });
                    } else {
                        Swal.fire({
                            title: 'Erro',
                            text: data.message || 'Não foi possível favoritar este item.',
                            icon: 'error'
                        });
                    }

                } catch (error) {
                    console.error("Erro ao favoritar:", error);
                    Swal.fire({
                        title: 'Erro',
                        text: 'Não foi possível conectar ao servidor.',
                        icon: 'error'
                    });
                }
            });

            card.appendChild(btn);
        }
    });
}

function salvarFavorito(postagem) {
    let favoritos = JSON.parse(localStorage.getItem("favoritos")) || [];

    const jaExiste = favoritos.some(f => f.id === postagem.id);

    if (!jaExiste) {
        favoritos.push(postagem);
        localStorage.setItem("favoritos", JSON.stringify(favoritos));

        Swal.fire({
            title: 'Item favoritado!',
            text: `${postagem.nomePostagem} foi adicionado aos favoritos.`,
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
        });
    } else {
        Swal.fire({
            title: 'Já favoritado',
            text: `${postagem.nomePostagem} já está nos seus favoritos.`,
            icon: 'info',
            timer: 2000,
            showConfirmButton: false
        });
    }
}
