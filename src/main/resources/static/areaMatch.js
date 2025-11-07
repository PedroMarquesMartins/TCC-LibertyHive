// Variáveis globais para controlar os matches
let matches = [];
let currentIndex = 0;

//conversao de byte em base64 para exibir imagem
function bytesParaBase64(bytes) {
    if (!bytes || bytes.length === 0) return '';
    const bytesArray = new Uint8Array(bytes);
    let binary = '';
    const chunkSize = 0x8000;
    for (let i = 0; i < bytesArray.length; i += chunkSize) {
        const chunk = bytesArray.subarray(i, i + chunkSize);
        binary += String.fromCharCode.apply(null, chunk);
    }
    return btoa(binary);
}
//Inicio da Pagina
document.addEventListener('DOMContentLoaded', function () {
    const token = localStorage.getItem('token');
    if (!token) {
        Swal.fire({
            icon: "warning",
            title: "Atenção",
            text: "Você precisa estar logado!",
        }).then(() => {
            window.location.href = 'login.html';
        });
        return;
    }
    buscarMatches(token);
});
//Função para buscar os matches no BANCO/API
async function buscarMatches(token) {
    const container = document.getElementById("matchesContainer");
    container.innerHTML = "<p>Carregando...</p>";

    try {
        const res = await fetch("http://localhost:8080/api/area-match/matches", {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) {
            container.innerHTML = `<p class="erro">Erro: ${res.status} - ${res.statusText}</p>`;
            return;
        }

        const data = await res.json();

        if (!Array.isArray(data) || data.length === 0) {
            container.innerHTML = "<p>Nenhum item compatível encontrado.</p>";
            return;
        }

        matches = data;
        currentIndex = 0;
        mostrarMatchAtual();

    } catch (err) {
        container.innerHTML = `<p class="erro">Erro ao buscar dados: ${err.message}</p>`;
        console.error(err);
    }
}

//Função para mostrar o match que foi feito
function mostrarMatchAtual() {
    const container = document.getElementById("matchesContainer");
    container.innerHTML = "";

    if (currentIndex >= matches.length) {
        container.innerHTML = "<p>Não há mais itens!</p>";
        return;
    }

    const item = matches[currentIndex];
    const div = document.createElement("div");
    div.className = "item";
    div.dataset.postagemId = item.id;

    if (item.imagem) {
        const img = document.createElement("img");
        img.src = "data:image/png;base64," + item.imagem;
        img.alt = item.nomePostagem;
        div.appendChild(img);
    }

    const categoriasInteresse = [
        item.categoriaInteresse1,
        item.categoriaInteresse2,
        item.categoriaInteresse3
    ].filter(cat => cat && cat.trim() !== "");

    const categoriasInteresseTexto = categoriasInteresse.length > 0
        ? `<strong>Categorias de Interesse:</strong> ${categoriasInteresse.join(', ')} <br>`
        : '';

    let avaliacaoTexto = '';
    if (item.avaliacaoUsuario !== null && item.avaliacaoUsuario !== undefined) {
        avaliacaoTexto = `<strong>Avaliação do Escambista:</strong> ${item.avaliacaoUsuario} estrela(s) <br>`;
    } else {
        avaliacaoTexto = `<strong>Avaliação do Escambista:</strong> N/A <br>`;
    }
//Formação do conteudo do CARD
    div.innerHTML += `
        <strong>Nome do usuário:</strong> ${item.userNome} <br>
        ${avaliacaoTexto}
        <strong>Nome do item:</strong> ${item.nomePostagem} <br>
        <strong>Categoria:</strong> ${item.categoria} <br>
        ${categoriasInteresseTexto}
        <strong>Tipo:</strong> ${item.isProdOuServico ? "Produto" : "Serviço"} <br>
        <strong>Doação:</strong> ${item.isDoacao ? "Sim" : "Não"} <br>
        <strong>Cidade/UF:</strong> ${item.cidade} / ${item.uf} <br>
    `;

    const btnSim = document.createElement("button");
    btnSim.textContent = "VER DETALHES";
    btnSim.onclick = () => {
        window.location.href = `detalhesItem.html?id=${item.id}`;
    };
    //Passar
    const btnNao = document.createElement("button");
    btnNao.textContent = "PASSAR";
    btnNao.onclick = () => {
        passarItem(item.id, div);
        currentIndex++;
        mostrarMatchAtual();
    };
    //Favoritar
    const btnFav = document.createElement("button");
    btnFav.textContent = "Favoritar ❤️";
    btnFav.onclick = async () => {
        await favoritarItem(item.id);
    };

    div.appendChild(btnSim);
    div.appendChild(btnFav);
    div.appendChild(btnNao);

    container.appendChild(div);
}


//Função de avançar para o próximo item
async function passarItem(itemOutroUsuarioId, cardElement) {
    const token = localStorage.getItem('token');
    if (!token) {
        Swal.fire({
            icon: "warning",
            title: "Atenção",
            text: "Você precisa estar logado!",
        }).then(() => {
            window.location.href = 'login.html';
        });
        return;
    }

    try {
        const res = await fetch(`http://localhost:8080/api/area-match/interesse?itemOutroUsuarioId=${itemOutroUsuarioId}&sim=false`, {
            method: "POST",
            headers: { "Authorization": "Bearer " + token }
        });

        const result = await res.json();

        if (!res.ok) {
            Swal.fire({
                icon: "error",
                title: "Erro",
                text: result.message || `Status ${res.status}`
            });
        }

    } catch (err) {
        console.error(err);
        Swal.fire({
            icon: "error",
            title: "Erro ao registrar passagem",
            text: err.message
        });
    }
}

async function favoritarItem(postagemId) {
    const token = localStorage.getItem('token');
    if (!token) {
        Swal.fire({
            icon: 'warning',
            title: 'Atenção',
            text: 'Você precisa estar logado!',
        }).then(() => {
            window.location.href = 'login.html';
        });
        return;
    }
//Envio de requisição
    try {
        const res = await fetch(`http://localhost:8080/api/area-match/favorito?postagemId=${postagemId}`, {
            method: "POST",
            headers: { "Authorization": "Bearer " + token }
        });

        const result = await res.json();
        Swal.fire({
            icon: res.ok ? 'success' : 'error',
            title: res.ok ? 'Favoritado!' : 'Oops...',
            text: result.message
        });
    } catch (err) {
        console.error(err);
        Swal.fire({
            icon: 'error',
            title: 'Erro ao favoritar',
            text: err.message
        });
    }
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userNome');
    localStorage.removeItem('userId');
    window.location.href = 'login.html';
}