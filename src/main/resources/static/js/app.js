function showLogin()
{
    document.getElementById(
        "loginBox"
    ).style.display = "block";

    document.getElementById(
        "registerBox"
    ).style.display = "none";
}


function showRegister()
{
    document.getElementById(
        "registerBox"
    ).style.display = "block";

    document.getElementById(
        "loginBox"
    ).style.display = "none";
}


async function register()
{
    const user =
    {
        name:
            document.getElementById(
                "regName"
            ).value,

        email:
            document.getElementById(
                "regEmail"
            ).value,

        password:
            document.getElementById(
                "regPassword"
            ).value,

        role:
            document.getElementById(
                "regRole"
            ).value
    };


    const response =
        await fetch(
            "/api/auth/register",
            {
                method: "POST",

                headers:
                {
                    "Content-Type":
                        "application/json"
                },

                body:
                    JSON.stringify(user)
            }
        );


    const result =
        await response.json();


    document.getElementById(
        "registerMessage"
    ).innerText =
        result.message;
}


async function login()
{
    const data =
    {
        email:
            document.getElementById(
                "loginEmail"
            ).value,

        password:
            document.getElementById(
                "loginPassword"
            ).value
    };


    const response =
        await fetch(
            "/api/auth/login",
            {
                method: "POST",

                headers:
                {
                    "Content-Type":
                        "application/json"
                },

                body:
                    JSON.stringify(data)
            }
        );


    const result =
        await response.json();


    document.getElementById(
        "loginMessage"
    ).innerText =
        result.message;


    if (result.user)
    {
        document.getElementById(
            "sellerId"
        ).value =
            result.user.id;
    }
}


async function addProduct()
{
    const product =
    {
        name:
            document.getElementById(
                "productName"
            ).value,

        description:
            document.getElementById(
                "productDescription"
            ).value,

        price:
            Number(
                document.getElementById(
                    "productPrice"
                ).value
            ),

        category:
            document.getElementById(
                "productCategory"
            ).value,

        stock:
            Number(
                document.getElementById(
                    "productStock"
                ).value
            ),

        sellerId:
            Number(
                document.getElementById(
                    "sellerId"
                ).value
            )
    };


    const response =
        await fetch(
            "/api/products",
            {
                method: "POST",

                headers:
                {
                    "Content-Type":
                        "application/json"
                },

                body:
                    JSON.stringify(product)
            }
        );


    const result =
        await response.json();


    document.getElementById(
        "productMessage"
    ).innerText =
        "Product added successfully";
}


async function loadProducts()
{
    const response =
        await fetch(
            "/api/products"
        );


    const products =
        await response.json();


    const container =
        document.getElementById(
            "products"
        );


    container.innerHTML = "";


    products.forEach(
        product =>
        {

            const div =
                document.createElement(
                    "div"
                );


            div.className =
                "product";


            div.innerHTML = `

                <h3>
                    ${product.name}
                </h3>

                <p>
                    ${product.description}
                </p>

                <p>
                    Category:
                    ${product.category}
                </p>

                <p>
                    Price:
                    ₹${product.price}
                </p>

                <p>
                    Stock:
                    ${product.stock}
                </p>

                <button
                    onclick="
                    deleteProduct(
                        ${product.id}
                    )"
                >
                    Delete
                </button>

            `;


            container.appendChild(
                div
            );

        }
    );
}


async function deleteProduct(id)
{
    await fetch(
        `/api/products/${id}`,
        {
            method: "DELETE"
        }
    );


    loadProducts();
}