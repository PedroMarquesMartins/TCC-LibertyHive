function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userNome');
    window.location.href = 'login.html';
}

function voltarInicio() {
    window.location.href = 'inicio.html';
}
document.addEventListener("DOMContentLoaded", async () => {
    const nome = localStorage.getItem("userNome") || "Usuário";
    document.getElementById("usuarioNome").textContent = nome;

    const lista = document.getElementById("listaFavoritos");

    const token = localStorage.getItem("token");
    if (!token) {
        lista.innerHTML = "<p class='text-danger text-center'>Você precisa estar logado para ver os favoritos.</p>";
        return;
    }
    try {
        const response = await fetch("http://localhost:8080/favoritos", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            lista.innerHTML = "<p class='text-danger text-center'>Erro ao carregar favoritos.</p>";
            return;
        }

        const favoritos = await response.json();

        if (favoritos.length === 0) {
            lista.innerHTML = "<p class='text-muted text-center'>Nenhum item nos favoritos ainda.</p>";
            return;
        }

        lista.innerHTML = "";

        favoritos.forEach((favorito, index) => {
            const postagem = favorito.postagem;
            if (!postagem) return;

            const col = document.createElement("div");
            col.className = "col-md-4 col-sm-6";

            col.innerHTML = `
    <div class="card h-100">
        <img src="${postagem.imagem ? `data:image/png;base64,${postagem.imagem}` : './imagens/placeholder.png'}"
             class="card-img-top" alt="${postagem.nomePostagem || 'Postagem'}">
        <div class="card-body">
            <h5 class="card-title">${postagem.nomePostagem || "Sem título"}</h5>
            <p class="card-text"><strong>Descrição:</strong> ${postagem.descricao || "Não informada"}</p>
            <p class="card-text"><strong>Categoria:</strong> ${postagem.categoria || "Não informada"}</p>
            <p class="card-text"><strong>Tipo:</strong> ${postagem.isProdOuServico ? "Produto" : "Serviço"}</p>
            <p class="card-text"><strong>Doação/Voluntário:</strong> ${postagem.doacao ? "Sim" : "Não"}</p>
            <p class="text-muted"><strong>Local:</strong> ${postagem.cidade || ""} - ${postagem.uf || ""}</p>
            <p class="text-muted"><strong>Data de criação:</strong> ${postagem.dataCriacao || ""}</p>
            <button class="btn btn-sm btn-danger btn-remove">
                <i class="bi bi-x-circle"></i> Remover
            </button>
        </div>
    </div>
`;


            col.querySelector(".btn-remove").addEventListener("click", async () => {
                const result = await Swal.fire({
                    title: `Remover "${postagem.nomePostagem}" dos favoritos?`,
                    icon: "warning",
                    showCancelButton: true,
                    confirmButtonText: "Sim, remover",
                    cancelButtonText: "Cancelar"
                });

                if (!result.isConfirmed) return;

                try {
                    const removeResp = await fetch(`http://localhost:8080/favoritos/${favorito.id}`, {
                        method: "DELETE",
                        headers: {
                            "Authorization": "Bearer " + token,
                            "Content-Type": "application/json"
                        }
                    });

                    const data = await removeResp.json();
                    if (removeResp.ok) {
                        col.remove();
                        Swal.fire("Removido!", data.message || "O favorito foi removido.", "success");
                    } else {
                        Swal.fire("Erro", data.message || "Erro ao remover favorito.", "error");
                    }
                } catch (err) {
                    console.error(err);
                    Swal.fire("Erro", "Erro de conexão ao remover favorito.", "error");
                }
            });


            lista.appendChild(col);
        });

    } catch (error) {
        console.error("Erro:", error);
        lista.innerHTML = "<p class='text-danger text-center'>Erro ao carregar favoritos.</p>";
    }
});
