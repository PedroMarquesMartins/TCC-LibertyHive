//Salvar formulário e Listar Itens etc
const categorias = [
    "Tecnologia",
    "Celulares e Smartphones",
    "Acessórios para Celulares (Capas, Películas, Carregadores)",
    "Peças para Celulares",
    "Smartwatches e Pulseiras",
    "Informática",
    "Notebooks e Laptops",
    "Computadores de Mesa (Desktops)",
    "Componentes e Peças (Processadores, Placas de Vídeo, Memórias RAM)",
    "Tablets e Acessórios",
    "Impressoras e Suprimentos",
    "Monitores",
    "Acessórios para PC (Teclados, Mouses, Webcams)",
    "Redes e Wi-Fi (Roteadores, Repetidores)",
    "Videogames",
    "Consoles (PlayStation, Xbox, Nintendo)",
    "Jogos Físicos e Digitais",
    "Acessórios para Consoles (Controles, Headsets)",
    "PC Gamer",
    "Eletrônicos, Áudio e Vídeo",
    "TVs e Smart TVs",
    "Equipamentos de Áudio (Home Theaters, Soundbars, Caixas de Som)",
    "Fones de Ouvido",
    "Câmeras Digitais e Drones",
    "Projetores e Telas",
    "Casa, Móveis e Decoração",
    "Móveis",
    "Móveis para Quarto (Camas, Guarda-Roupas, Cômodas)",
    "Móveis para Sala de Estar (Sofás, Racks, Estantes)",
    "Móveis para Cozinha e Sala de Jantar (Mesas, Cadeiras, Armários)",
    "Móveis para Escritório",
    "Decoração",
    "Cortinas e Persianas",
    "Quadros, Vasos e Enfeites",
    "Tapetes",
    "Iluminação (Lustres, Luminárias, Lâmpadas)",
    "Cama, Mesa e Banho",
    "Roupas de Cama",
    "Toalhas de Banho e Rosto",
    "Toalhas de Mesa e Acessórios",
    "Utilidades Domésticas",
    "Utensílios de Cozinha (Panelas, Talheres, Potes)",
    "Organização da Casa (Caixas, Prateleiras, Cabides)",
    "Limpeza da Casa",
    "Jardim e Área Externa",
    "Móveis de Jardim",
    "Ferramentas de Jardinagem",
    "Churrasqueiras e Acessórios",
    "Piscinas e Acessórios",
    "Eletrodomésticos",
    "Cozinha",
    "Geladeiras e Freezers",
    "Fogões, Fornos e Cooktops",
    "Micro-ondas",
    "Lava-louças",
    "Pequenos Eletrodomésticos",
    "Cafeteiras",
    "Liquidificadores e Batedeiras",
    "Fritadeiras Elétricas (Air Fryer)",
    "Sanduicheiras e Grills",
    "Climatização e Cuidados com a Casa",
    "Ar Condicionado e Climatizadores",
    "Ventiladores e Circuladores de Ar",
    "Aspiradores de Pó e Robôs",
    "Máquinas de Lavar e Secar Roupas",
    "Ferros de Passar",
    "Moda e Acessórios",
    "Roupas",
    "Roupas Femininas (Vestidos, Blusas, Calças)",
    "Roupas Masculinas (Camisetas, Camisas, Bermudas)",
    "Moda Íntima e Lingerie",
    "Moda Praia",
    "Calçados",
    "Tênis",
    "Sapatos Sociais e Casuais",
    "Sandálias e Chinelos",
    "Botas",
    "Bolsas e Malas",
    "Bolsas Femininas",
    "Mochilas",
    "Malas de Viagem",
    "Acessórios",
    "Joias e Bijuterias",
    "Relógios",
    "Óculos de Sol e de Grau",
    "Cintos, Chapéus e Bonés",
    "Beleza e Cuidado Pessoal",
    "Perfumes",
    "Perfumes Femininos",
    "Perfumes Masculinos",
    "Maquiagem",
    "Rosto (Base, Corretivo, Pó)",
    "Olhos (Máscara de Cílios, Sombra, Delineador)",
    "Lábios (Batom, Gloss)",
    "Cuidados com a Pele (Skincare)",
    "Limpeza de Pele",
    "Hidratantes Faciais e Corporais",
    "Protetores Solares",
    "Cuidados com o Cabelo",
    "Shampoos e Condicionadores",
    "Tratamentos Capilares",
    "Modeladores e Finalizadores",
    "Barbearia e Cuidados Masculinos",
    "Esportes e Fitness",
    "Roupas e Calçados Esportivos",
    "Equipamentos de Academia e Musculação",
    "Suplementos Alimentares e Vitaminas",
    "Ciclismo",
    "Bicicletas",
    "Acessórios para Ciclistas (Capacetes, Luvas)",
    "Futebol, Vôlei e Basquete",
    "Lutas e Artes Marciais",
    "Esportes Aquáticos",
    "Brinquedos e Hobbies",
    "Brinquedos",
    "Bonecos e Bonecas",
    "Jogos de Tabuleiro e Cartas",
    "Blocos de Montar e Construção",
    "Veículos de Brinquedo",
    "Brinquedos para Bebês",
    "Hobbies",
    "Instrumentos Musicais",
    "Modelismo e Miniaturas",
    "Arte e Papelaria",
    "Colecionáveis (Figurinhas, Moedas, Selos)",
    "Veículos, Peças e Acessórios",
    "Carros e Caminhonetes",
    "Motos",
    "Peças para Veículos",
    "Motor e Componentes",
    "Freios e Suspensão",
    "Peças de Lataria",
    "Acessórios para Veículos",
    "Pneus e Rodas",
    "Som e Multimídia Automotivo",
    "GPS e Segurança",
    "Náutica (Barcos, Lanchas, Jet Skis)",
    "Cultura e Entretenimento",
    "Livros, Revistas e HQs",
    "Filmes e Séries (DVDs, Blu-ray)",
    "Música (CDs, Discos de Vinil)",
    "Antiguidades e Peças de Coleção",
    "Serviços",
    "Serviços Técnicos (Informática, Conserto de Eletrodomésticos)",
    "Aulas e Cursos (Idiomas, Música, Reforço Escolar)",
    "Consultoria (Negócios, Marketing, Jurídica)",
    "Design e Multimídia (Criação de Logos, Edição de Vídeo)",
    "Eventos e Festas (Fotografia, Buffet, Decoração)",
    "Reforma e Construção",
    "Imóveis",
    "Venda",
    "Apartamentos",
    "Casas e Sobrados",
    "Terrenos e Lotes",
    "Aluguel",
    "Apartamentos",
    "Casas e Sobrados",
    "Imóveis Comerciais",
    "Outra Categoria"
];
function criarSelectInteresse(id) {
    const select = document.createElement("select");
    select.name = "categoriaInteresse" + id;
    select.dataset.index = id;
    select.innerHTML = `<option value="">Selecione...</option>
<option value="Nenhuma">Nenhuma</option>` +
        categorias.map(cat => `<option value="${cat}">${cat}</option>`).join("");
    select.addEventListener("change", onChangeCategoriaInteresse);
    return select;
}
//chamada ao mudar uma categoria de interesse
function onChangeCategoriaInteresse(e) {
    const select = e.target;
    const container = document.getElementById("interessesContainer");
    const selects = [...container.querySelectorAll("select")];

    if ((select.value === "" || select.value === "Nenhuma") && select !== selects[selects.length - 1]) {
        for (let i = selects.length - 1; i > select.dataset.index; i--) {
            selects[i].remove();
        }
        return;
    }
//Adicionar novo select se for o último e tiver valor selecionado
    if (
        select.value !== "" &&
        select.value !== "Nenhuma" &&
        select === selects[selects.length - 1] &&
        selects.length < 3
    ) {
        container.appendChild(criarSelectInteresse(selects.length));
    }

    atualizarOpcoes();
}
//Atualizar opções para evitar duplicatas
function atualizarOpcoes() {
    const container = document.getElementById("interessesContainer");
    const selects = [...container.querySelectorAll("select")];
    const selecionadas = selects.map(s => s.value).filter(v => v !== "" && v !== "Nenhuma");

    selects.forEach(select => {
        const currentValue = select.value;
        [...select.options].forEach(option => {
            option.disabled = option.value !== currentValue && selecionadas.includes(option.value) && option.value !== "" && option.value !== "Nenhuma";
        });
    });
}
//Iniciar os selects de categorias de interesse
function iniciarCategoriasInteresse() {
    const container = document.getElementById("interessesContainer");
    container.innerHTML = "";
    container.appendChild(criarSelectInteresse(0));
}

function voltarInicio() {
    window.location.href = "inicio.html";
}

function mostrarFormulario() {
    document.getElementById("formAdicionar").style.display = "block";
}

function getTipoItem() {
    const selecionado = document.querySelector('input[name="tipoItem"]:checked');
    return selecionado ? selecionado.value : null;
}

function getIsDoacao() {
    const selecionado = document.querySelector('input[name="isDoacao"]:checked');
    return selecionado ? selecionado.value : null;
}
//Mostrar ou esconder interesses baseado na doação ou não
document.addEventListener("change", function (e) {
    if (e.target.name === "isDoacao") {
        const container = document.getElementById("interessesContainer");
        const label = document.getElementById("labelInteresses");
        if (e.target.value === "1") {
            container.style.display = "none";
            if (label) label.style.display = "none";
        } else {
            container.style.display = "block";
            if (label) label.style.display = "block";
            iniciarCategoriasInteresse();
        }
    }
});




//Salvar ou editar item
function salvarItem() {
    const token = localStorage.getItem('token');
    if (!token) {
        Swal.fire({
            icon: 'error',
            title: 'Sessão inválida',
            text: 'Por favor, faça o login novamente.',
        }).then(() => { window.location.href = 'login.html'; });
        return;
    }

    const isDoacao = getIsDoacao();
    const tipoItem = getTipoItem();

    if (!tipoItem || isDoacao === null) {
        Swal.fire({ icon: 'warning', title: 'Campos obrigatórios', text: 'Preencha todos os campos.' });
        return;
    }
            //Pegar os valores do formulário
    const categoria = document.getElementById("categoria").value;
    const nomeItem = document.getElementById("nomeItem").value.trim();
    const descricaoItem = document.getElementById("descricaoItem").value.trim();
    const uf = document.getElementById("ufItem").value;
    const cidade = document.getElementById("cidadeItem").value.trim();

    if (!categoria || !nomeItem || !descricaoItem || !uf || !cidade) {
        Swal.fire({ icon: 'warning', title: 'Campos obrigatórios', text: 'Preencha todos os campos.' });
        return;
    }
//Montar os dados para envio
    const container = document.getElementById("interessesContainer");
    const selects = [...container.querySelectorAll("select")];
    const categoriasEscolhidas = selects.map(s => s.value).filter(v => v !== "" && v !== "Nenhuma");

    if ((!isDoacao || isDoacao === "0") && categoriasEscolhidas.length === 0) {
        Swal.fire({ icon: 'warning', title: 'Campo obrigatório', text: 'Selecione ao menos uma categoria de interesse.' });
        return;
    }
        //Preparar FormData para envio
    const [cat1, cat2, cat3] = [
        categoriasEscolhidas[0] || "",
        categoriasEscolhidas[1] || "",
        categoriasEscolhidas[2] || ""
    ];
//Enviar para o backend
    const formData = new FormData();
    formData.append("userNome", localStorage.getItem("userNome"));
    formData.append("isProdOuServico", tipoItem === "Produto" ? 1 : 0);
    formData.append("isDoacao", isDoacao);
    formData.append("nomePostagem", nomeItem);
    formData.append("descricao", descricaoItem);
    formData.append("categoria", categoria);
    formData.append("categoriaInteresse1", cat1);
    formData.append("categoriaInteresse2", cat2);
    formData.append("categoriaInteresse3", cat3);
    formData.append("uf", uf);
    formData.append("cidade", cidade);

    const capa = document.getElementById("imagemCapa").files[0];
    if (capa) formData.append("imagem", capa);

    const secundarias = document.querySelectorAll(".imagemSecundaria");
    secundarias.forEach((input, i) => {
        if (input.files[0]) formData.append("imagensSecundarias", input.files[0]);
    });

    const url = idEditando ? `http://localhost:8080/api/postagens/${idEditando}` : "http://localhost:8080/api/postagens";
    const method = idEditando ? "PUT" : "POST";

    fetch(url, {
        method: method,
        body: formData,
        headers: { "Authorization": "Bearer " + token }
    })
    .then(res => {
        if (!res.ok) return res.json().then(err => { throw new Error(err.error || "Erro no servidor"); });
        return res.json();
    })
    .then(data => {
        Swal.fire({
            icon: "success",
            title: idEditando ? "Postagem atualizada!" : "Postagem criada!",
            showConfirmButton: false,
            timer: 1500
        });
        idEditando = null;
        document.getElementById("formAdicionar").reset();
        iniciarCategoriasInteresse();
        carregarPostagens();
        fecharFormulario();
    })
    .catch(err => Swal.fire("Erro", err.message, "error"));
}


function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userNome');
    window.location.href = 'login.html';
}
//Converter array de bytes para base64 para exibição de imagens
function bytesParaBase64(bytes) {
    if (!bytes) return '';
    let binary = '';
    for (let i = 0; i < bytes.length; i++) {
        binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary);
}
//Preencher o formulario com os dados da postagem para edicao
function preencherFormulario(p) {
    document.getElementById("nomeItem").value = p.nomePostagem;
    document.getElementById("descricaoItem").value = p.descricao;
    document.getElementById("categoria").value = p.categoria;
    document.getElementById("ufItem").value = p.uf;
    document.getElementById("cidadeItem").value = p.cidade;
    document.querySelector(`input[name="tipoItem"][value="${p.isProdOuServico ? 'Produto' : 'Servico'}"]`).checked = true;
    document.querySelector(`input[name="isDoacao"][value="${p.doacao ? '1' : '0'}"]`).checked = true;
    // Iniciar categorias de interesse
    iniciarCategoriasInteresse();
    const container = document.getElementById("interessesContainer");
    const selects = container.querySelectorAll("select");
    if (selects[0]) selects[0].value = p.categoriaInteresse1 || "";
    if (selects[1]) selects[1].value = p.categoriaInteresse2 || "";
    if (selects[2]) selects[2].value = p.categoriaInteresse3 || "";
// Atualizar opções para evitar duplicatas
    if (p.imagem) {
        const imgCapa = document.getElementById("previewCapa");
        imgCapa.src = `data:image/png;base64,${p.imagem}`;
        aplicarEstilo(imgCapa);
    }
    const imgsSec = [p.imagemS01, p.imagemS02, p.imagemS03, p.imagemS04, p.imagemS05];
    const inputsSec = document.querySelectorAll(".imagemSecundaria");
    const previewsSec = document.querySelectorAll(".previewSecundaria");
    for (let i = 0; i < 5; i++) {
        if (imgsSec[i]) {
            previewsSec[i].src = `data:image/png;base64,${imgsSec[i]}`;
            aplicarEstilo(previewsSec[i]);
        } else {
            previewsSec[i].src = "";
        }
    }
}

//Variavel para rastrear se estamos editando uma postagem
let idEditando = null; 

function carregarPostagens() { //Carregar as postagens do usuário chamando o backend e populando a lista
    const token = localStorage.getItem("token");
    if (!token) return;

    fetch("http://localhost:8080/api/postagens/listar", {
        method: "GET",
        headers: { "Authorization": "Bearer " + token }
    })
        .then(res => {
            if (!res.ok) throw new Error("Falha ao carregar postagens: " + res.status);
            return res.json();
        })
        .then(postagens => {
            const container = document.getElementById("listaPostagens");
            container.innerHTML = "";

            if (postagens.length === 0) {
                container.textContent = "Você ainda não tem postagens.";
                return;
            }

            postagens.forEach(p => {
                const div = document.createElement("div");
                div.classList.add("postagem");

                if (p.imagem) {
                    const img = document.createElement("img");
                    img.src = `data:image/png;base64,${p.imagem}`;
                    img.alt = p.nomePostagem;
                    aplicarEstilo(img);
                    div.appendChild(img);
                }

                const h3 = document.createElement("h3");
                h3.textContent = p.nomePostagem;
                div.appendChild(h3);

                const pDesc = document.createElement("p");
                pDesc.innerHTML = `<span>Descrição:</span> ${p.descricao}`;
                div.appendChild(pDesc);

                const info = document.createElement("p");
                info.innerHTML = `<span>Categoria:</span> ${p.categoria} - <span>Tipo:</span> ${p.isProdOuServico ? "Produto" : "Serviço"}`;
                div.appendChild(info);

                const doacaoo = document.createElement("p");
                doacaoo.innerHTML = `<span>Doação/Voluntário:</span> ${p.doacao ? "Sim" : "Não"}`;
                div.appendChild(doacaoo);

                const localidade = document.createElement("p");
                localidade.innerHTML = `<span>Local:</span> ${p.cidade} - ${p.uf}`;
                div.appendChild(localidade);
                const btnExcluir = document.createElement("button");
                btnExcluir.textContent = "Excluir";
                btnExcluir.style.marginTop = "5px";
                btnExcluir.addEventListener("click", () => {
                    Swal.fire({
                        title: `Deseja realmente excluir a postagem "${p.nomePostagem}"?`,
                        icon: "warning",
                        showCancelButton: true,
                        confirmButtonText: "Sim, excluir",
                        cancelButtonText: "Cancelar"
                    }).then((result) => {
                        if (result.isConfirmed) {
                            fetch(`http://localhost:8080/api/postagens/${p.id}`, {
                                method: "DELETE",
                                headers: { "Authorization": "Bearer " + token }
                            })
                                .then(res => {
                                    if (!res.ok) throw new Error("Erro ao excluir postagem");
                                    carregarPostagens();
                                    Swal.fire("Excluído!", "A postagem foi excluída.", "success");
                                })
                                .catch(err => Swal.fire("Erro", err.message, "error"));
                        }
                    });
                });
                div.appendChild(btnExcluir);

                const btnEditar = document.createElement("button");
                btnEditar.textContent = "Editar";
                btnEditar.style.marginTop = "5px";
                btnEditar.style.marginLeft = "5px";
                btnEditar.addEventListener("click", () => {
                    idEditando = p.id;
                    mostrarFormulario();
                    preencherFormulario(p);
                });
                div.appendChild(btnEditar);

                container.appendChild(div);
            });
        })
        .catch(err => {
            console.error(err);
            alert("Falha ao carregar suas postagens.");
        });
}

document.addEventListener("DOMContentLoaded", function () {
    const nome = localStorage.getItem("userNome");
    if (!nome) {
        window.location.href = "login.html";
        return;
    }
    document.getElementById("usuarioNome").textContent = nome;
    iniciarCategoriasInteresse();
    carregarPostagens();
});

function mostrarFormulario() {
    document.getElementById("popupFormulario").style.display = "flex";
}

function fecharFormulario() {
    document.getElementById("popupFormulario").style.display = "none";
    document.addEventListener("keydown", function (e) {
        if (e.key === "Escape") {
            const popup = document.getElementById("popupFormulario");
            if (popup && popup.style.display === "flex") {
                fecharFormulario();
            }
        }
    });

}

const estiloPreview = {
    width: "150px",
    height: "150px",
    objectFit: "cover",
    border: "2px solid #000000ff",
    borderRadius: "5px",
    display: "block",
    marginTop: "10px"
};

function aplicarEstilo(img) {
    for (const prop in estiloPreview) {
        img.style[prop] = estiloPreview[prop];
    }
}
//Preview de imagens ao selecionar
document.getElementById("imagemCapa").addEventListener("change", function () {
    const img = document.getElementById("previewCapa");
    if (this.files && this.files[0]) {
        const reader = new FileReader();
        reader.onload = function (e) {
            img.src = e.target.result;
            aplicarEstilo(img);
        };
        reader.readAsDataURL(this.files[0]);
    } else {
        img.src = "";
        img.style.display = "none";
    }
});
document.querySelectorAll(".imagemSecundaria").forEach(input => {
    input.addEventListener("change", function () {
        const img = this.parentElement.querySelector(".previewSecundaria");
        if (this.files && this.files[0]) {
            const reader = new FileReader();
            reader.onload = function (e) {
                img.src = e.target.result;
                aplicarEstilo(img);
            };
            reader.readAsDataURL(this.files[0]);
        } else {
            img.src = "";
            img.style.display = "none";
        }
    });
});
