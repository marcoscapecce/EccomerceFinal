document.addEventListener("DOMContentLoaded", () => {
  const API_URL = "/api";
  const USER_ID = 5;
  const productsContainer = document.getElementById("productos-container");
  const cartSidebar = document.getElementById("cart-sidebar");
  const cartOverlay = document.getElementById("cart-overlay");

  async function leerRespuesta(response) {
    let data;
    try {
      data = await response.json();
    } catch (error) {
      if (!response.ok) throw new Error("La solicitud no pudo completarse.");
      throw new Error("La respuesta del servidor no tiene un formato válido.");
    }

    if (!response.ok) {
      throw new Error(data.mensaje || "La solicitud no pudo completarse.");
    }
    return data;
  }

  async function obtenerProductos() {
    productsContainer.textContent = "Cargando productos...";
    try {
      const response = await fetch(`${API_URL}/productos`);
      cargarProductos(await leerRespuesta(response));
    } catch (error) {
      productsContainer.textContent = `No se pudieron cargar los productos: ${error.message}`;
    }
  }

  function cargarProductos(productos) {
    productsContainer.replaceChildren();
    if (!productos || productos.length === 0) {
      productsContainer.textContent = "No hay productos disponibles en la base de datos.";
      return;
    }

    productos.forEach((producto) => {
      const card = document.createElement("article");
      card.className = "card shadow-sm";

      const image = document.createElement("img");
      image.src = producto.urlImagen || "https://dummyimage.com/300x300/cccccc/000000.png";
      image.alt = producto.nombre;
      image.className = "card-img-top p-2";
      card.appendChild(image);

      const body = document.createElement("div");
      body.className = "card-body d-flex flex-column";
      const details = document.createElement("div");

      const badge = document.createElement("span");
      badge.className = "badge bg-secondary mb-2";
      badge.textContent = `ID: #${producto.id} | ${producto.categoria}`;
      details.appendChild(badge);

      const title = document.createElement("h5");
      title.className = "card-title";
      title.textContent = producto.nombre;
      details.appendChild(title);

      const description = document.createElement("p");
      description.className = "card-text text-muted small";
      description.textContent = producto.descripcion || "Producto de excelente calidad.";
      details.appendChild(description);
      body.appendChild(details);

      const purchaseInfo = document.createElement("div");
      purchaseInfo.className = "mt-auto";
      const stock = document.createElement("p");
      stock.className = "card-text mb-1";
      stock.textContent = `Stock: ${producto.stock}`;
      purchaseInfo.appendChild(stock);

      const price = document.createElement("p");
      price.className = "card-text fs-5 text-primary";
      price.textContent = `$${Number(producto.precio).toFixed(2)}`;
      purchaseInfo.appendChild(price);

      const addButton = document.createElement("button");
      addButton.type = "button";
      addButton.className = "btn btn-primary w-100 mt-2";
      addButton.textContent = "Agregar al carrito";
      addButton.disabled = producto.stock <= 0;
      addButton.addEventListener("click", () => agregarAlCarrito(producto, addButton));
      purchaseInfo.appendChild(addButton);

      body.appendChild(purchaseInfo);
      card.appendChild(body);
      productsContainer.appendChild(card);
    });
  }

  function leerCarrito() {
    return JSON.parse(localStorage.getItem("cart") || "[]");
  }

  function agregarAlCarrito(producto, button) {
    const cart = leerCarrito();
    const existingProduct = cart.find((item) => item.id === producto.id);
    if (existingProduct && existingProduct.quantity >= producto.stock) {
      alert("No puedes agregar más unidades que el stock disponible.");
      return;
    }
    if (producto.stock <= 0) {
      alert("Este producto no tiene stock disponible.");
      return;
    }
    if (existingProduct) {
      existingProduct.quantity++;
    } else {
      cart.push({
        id: producto.id,
        title: producto.nombre,
        price: producto.precio,
        quantity: 1
      });
    }
    localStorage.setItem("cart", JSON.stringify(cart));
    actualizarCarrito();

    button.textContent = "Agregado";
    button.classList.replace("btn-primary", "btn-success");
    button.disabled = true;
    setTimeout(() => {
      button.textContent = "Agregar al carrito";
      button.classList.replace("btn-success", "btn-primary");
      button.disabled = producto.stock <= 0;
    }, 1000);
  }

  function actualizarCarrito() {
    const cart = leerCarrito();
    const cartItems = document.getElementById("carrito-items");
    const checkoutButton = document.getElementById("realizar-compra");
    cartItems.replaceChildren();
    let total = 0;
    const itemCount = cart.reduce((sum, item) => sum + item.quantity, 0);

    if (cart.length === 0) {
      const emptyMessage = document.createElement("p");
      emptyMessage.className = "text-center text-muted py-3";
      emptyMessage.textContent = "Tu carrito está vacío";
      cartItems.appendChild(emptyMessage);
    } else {
      cart.forEach((item) => {
        total += item.price * item.quantity;
        const row = document.createElement("div");
        row.className = "cart-item";
        const details = document.createElement("div");
        details.className = "cart-item-details";

        const title = document.createElement("div");
        title.className = "cart-item-title";
        title.textContent = item.title;
        details.appendChild(title);

        const quantity = document.createElement("div");
        quantity.className = "cart-item-info";
        quantity.textContent = `Cantidad: ${item.quantity}`;
        details.appendChild(quantity);

        const price = document.createElement("div");
        price.className = "cart-item-price";
        price.textContent = `$${(item.price * item.quantity).toFixed(2)}`;
        details.appendChild(price);
        row.appendChild(details);
        cartItems.appendChild(row);
      });
    }

    document.getElementById("carrito-total").textContent = total.toFixed(2);
    document.getElementById("cart-counter").textContent = itemCount;
    document.getElementById("cart-counter-nav").textContent = itemCount;
    checkoutButton.disabled = cart.length === 0;
  }

  function mostrarCarrito(mostrar) {
    cartSidebar.classList.toggle("hidden", !mostrar);
    cartSidebar.setAttribute("aria-hidden", String(!mostrar));
    cartOverlay.classList.toggle("active", mostrar);
    cartOverlay.setAttribute("aria-hidden", String(!mostrar));
    document.body.classList.toggle("cart-open", mostrar);
    document.getElementById("open-cart").setAttribute("aria-expanded", String(mostrar));
  }

  document.getElementById("btn-productos").addEventListener("click", obtenerProductos);

  document.getElementById("open-cart").addEventListener("click", (event) => {
    event.preventDefault();
    mostrarCarrito(cartSidebar.classList.contains("hidden"));
  });
  cartOverlay.addEventListener("click", () => mostrarCarrito(false));

  document.getElementById("form-nuevo-producto").addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const nuevoProducto = {
      nombre: document.getElementById("prod-nombre").value.trim(),
      descripcion: document.getElementById("prod-descripcion").value.trim(),
      precio: Number(document.getElementById("prod-precio").value),
      categoria: document.getElementById("prod-categoria").value,
      urlImagen: document.getElementById("prod-imagen").value.trim(),
      stock: Number(document.getElementById("prod-stock").value)
    };

    try {
      const response = await fetch(`${API_URL}/productos`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(nuevoProducto)
      });
      await leerRespuesta(response);
      form.reset();
      if (typeof bootstrap !== "undefined" && bootstrap.Modal) {
        bootstrap.Modal.getOrCreateInstance(document.getElementById("agregarProductoModal")).hide();
      }
      await obtenerProductos();
    } catch (error) {
      alert(`Error: ${error.message}`);
    }
  });

  document.getElementById("realizar-compra").addEventListener("click", async () => {
    const cart = leerCarrito();
    const pedido = Object.fromEntries(cart.map((item) => [item.id, item.quantity]));
    try {
      const response = await fetch(`${API_URL}/pedidos`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(pedido)
      });
      const pedidoProcesado = await leerRespuesta(response);
      localStorage.removeItem("cart");
      actualizarCarrito();
      await obtenerProductos();
      const total = Number(pedidoProcesado.costoTotal).toFixed(2);
      if (typeof bootstrap !== "undefined" && bootstrap.Modal) {
        document.getElementById("modal-total").textContent = total;
        bootstrap.Modal.getOrCreateInstance(document.getElementById("compraExitosaModal")).show();
      } else {
        alert(`¡Pedido realizado con éxito! Total: $${total}`);
      }
    } catch (error) {
      alert(`No se pudo realizar el pedido: ${error.message}`);
    }
  });

  document.getElementById("vaciar-carrito").addEventListener("click", () => {
    localStorage.removeItem("cart");
    actualizarCarrito();
  });

  document.getElementById("btn-ver-pedidos").addEventListener("click", async (event) => {
    event.preventDefault();
    const historySection = document.getElementById("pedidos-historial");
    const historyContainer = document.getElementById("historial-container");
    historySection.style.display = "block";
    historyContainer.textContent = "Cargando historial...";

    try {
      const response = await fetch(`${API_URL}/usuarios/${USER_ID}/pedidos`);
      const pedidos = await leerRespuesta(response);
      historyContainer.replaceChildren();
      if (pedidos.length === 0) {
        historyContainer.textContent = "Aún no realizaste ningún pedido.";
        return;
      }

      pedidos.forEach((pedido) => {
        const card = document.createElement("article");
        card.className = "col-md-8 mb-3";
        const content = document.createElement("div");
        content.className = "card w-100 shadow-sm";
        const body = document.createElement("div");
        body.className = "card-body";

        const heading = document.createElement("h5");
        heading.textContent = `Pedido #${pedido.id}`;
        body.appendChild(heading);

        const date = document.createElement("p");
        date.textContent = `Fecha: ${new Date(pedido.fechaCreacion).toLocaleString()}`;
        body.appendChild(date);

        const status = document.createElement("p");
        status.textContent = `Estado: ${pedido.estado}`;
        body.appendChild(status);

        const products = document.createElement("p");
        products.textContent = `Productos: ${pedido.lineas
          .map((linea) => `${linea.producto.nombre} (x${linea.cantidad})`)
          .join(", ")}`;
        body.appendChild(products);

        const total = document.createElement("p");
        total.className = "text-success";
        total.textContent = `Costo total: $${Number(pedido.costoTotal).toFixed(2)}`;
        body.appendChild(total);

        content.appendChild(body);
        card.appendChild(content);
        historyContainer.appendChild(card);
      });
      historySection.scrollIntoView({ behavior: "smooth" });
    } catch (error) {
      historyContainer.textContent = `No se pudo cargar el historial: ${error.message}`;
    }
  });

  document.getElementById("btn-salir").addEventListener("click", (event) => {
    event.preventDefault();
    localStorage.removeItem("cart");
    sessionStorage.clear();
    alert("Sesión de navegación finalizada. Se eliminó el carrito local.");
    location.reload();
  });

  obtenerProductos();
  actualizarCarrito();
});
