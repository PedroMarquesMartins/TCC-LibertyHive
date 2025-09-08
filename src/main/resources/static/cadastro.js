//Leitura das entradas
const emailInput = document.getElementById('email');
const userNomeInput = document.getElementById('userNome');
const senhaInput = document.getElementById('senha');

const emailAt = document.getElementById('emailAt');
const emailSpace = document.getElementById('emailSpace');
const userLength = document.getElementById('userLength');
const userFormat = document.getElementById('userFormat');
const senhaLength = document.getElementById('senhaLength');
const senhaNotEmpty = document.getElementById('senhaNotEmpty');

emailInput.addEventListener('input', () => {
    emailAt.classList.toggle('valid', emailInput.value.includes('@'));
    emailSpace.classList.toggle('valid', !emailInput.value.includes(' '));
});

userNomeInput.addEventListener('input', () => {
    const val = userNomeInput.value.trim();
    userLength.classList.toggle('valid', val.length >= 3 && val.length <= 10);
    userFormat.classList.toggle('valid', /^[A-Za-z0-9]+$/.test(val));
});

senhaInput.addEventListener('input', () => {
    const val = senhaInput.value;
    senhaLength.classList.toggle('valid', val.length >= 6);
    senhaNotEmpty.classList.toggle('valid', val.trim().length > 0);
});
document.getElementById('formCadastro').addEventListener('submit', function (event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const userNome = document.getElementById('userNome').value;

    const dados = {
        email: email,
        senha: senha,
        userNome: userNome
    };
    //Envio para o cadastro (rota http)
    fetch('http://localhost:8080/api/cadastros', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(dados)
    })
        .then(async response => {
            const data = await response.json();

            if (!response.ok) {
                document.getElementById('mensagemErro').textContent = data.error || data.message || 'Erro desconhecido no cadastro.';
                throw new Error(data.error || data.message || 'Erro no cadastro');
            }

            document.getElementById('mensagemErro').textContent = '';
            return data;
        })
        .then(data => {
            Swal.fire({
                icon: 'success',
                title: 'Cadastro realizado com sucesso!',
                showConfirmButton: false,
                timer: 1000
            });
            document.getElementById('formCadastro').reset();
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 1000);
        })
        .catch(error => {
            console.error('Erro:', error);
        });
});