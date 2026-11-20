document.addEventListener("DOMContentLoaded", () => {
  const API_URL = "/api";

  // 2. Cargar productos desde Java (GET /api/productos)
  function obtenerProductos() {
    fetch(`${API_URL}/productos`)
      .then((response) => {
        if (!response.ok) throw new Error("API de Java no disponible");
        return response.json();
      })
      .then((productos) => cargarProductos(productos))
      .catch((error) => console.error("Error al obtener productos:", error));
  }

  function cargarProductos(data) {
    const productosContainer = document.getElementById("productos-container");
    productosContainer.innerHTML = "";

    if (!data || data.length === 0) {
      productosContainer.innerHTML = '<p class="text-center text-muted w-100">No hay productos disponibles en la base de datos.</p>';
      return;
    }

    data.forEach((producto) => {
      const descripcionCorta = producto.descripcion || "Producto de excelente calidad.";
      const imagenUrl = producto.urlImagen || "https://dummyimage.com";

      productosContainer.innerHTML += `
        <div class="card shadow-sm">
          <img src="${imagenUrl}" class="card-img-top p-2" alt="${producto.nombre}">
          <div class="card-body d-flex flex-column">
            <div>
              <span class="badge bg-secondary mb-2">ID: #${producto.id} | ${producto.categoria}</span>
              <h5 class="card-title">${producto.nombre}</h5>
              <p class="card-text text-muted small">${descripcionCorta}</p>
            </div>
            <div class="mt-auto">
              <p class="card-text mb-1">Stock: <strong>${producto.stock}</strong></p>
              <p class="card-text fs-5 text-primary"><strong>$${producto.precio.toFixed(2)}</strong></p>
              <button class="btn btn-primary w-100 mt-2" onclick="addToCart(${producto.id}, '${producto.nombre}', ${producto.precio}, this)">
                Agregar al carrito
              </button>
            </div>
          </div>
        </div>
      `;
    });
  }

  // 3. Guardar nuevo producto (POST /api/productos)
  const formNuevoProducto = document.getElementById("form-nuevo-producto");
  if (formNuevoProducto) {
    formNuevoProducto.addEventListener("submit", (e) => {
      e.preventDefault();

      const nuevoProducto = {
        nombre: document.getElementById("prod-nombre").value,
        descripcion: document.getElementById("prod-descripcion").value,
        precio: parseFloat(document.getElementById("prod-precio").value),
        categoria: document.getElementById("prod-categoria").value,
        urlImagen: document.getElementById("prod-imagen").value,
        stock: parseInt(document.getElementById("prod-stock").value)
      };

      fetch(`${API_URL}/productos`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(nuevoProducto)
      })
      .then(async (response) => {
        if (!response.ok) {
          const err = await response.json();
          throw new Error(err.mensaje || "Error al guardar producto");
        }
        return response.json();
      })
      .then(() => {
        formNuevoProducto.reset();
        const modalEl = document.getElementById("agregarProductoModal");
        if (typeof bootstrap !== "undefined") {
          const modal = bootstrap.Modal.getInstance(modalEl);
          if (modal) modal.hide();
        }
        obtenerProductos();
      })
      .catch((error) => alert("Error: " + error.message));
    });
  }


  window.addToCart = function (id, title, price, button) {
    let cart = JSON.parse(localStorage.getItem("cart")) || [];
    let existingProduct = cart.find((product) => product.id === id);
    if (existingProduct) {
      existingProduct.quantity++;
    } else {
      cart.push({ id, title, price, quantity: 1 });
    }
    localStorage.setItem("cart", JSON.stringify(cart));
    updateCartUI();

    button.textContent = "Agregado";
    button.className = "btn btn-success w-100 mt-2";
    button.disabled = true;
    setTimeout(() => {
      button.textContent = "Agregar al carrito";
      button.className = "btn btn-primary w-100 mt-2";
      button.disabled = false;
    }, 1000);
  };

  function updateCartUI() {
    const cart = JSON.parse(localStorage.getItem("cart")) || [];
    const carritoItems = document.getElementById("carrito-items");
    const comprarBtn = document.getElementById("realizar-compra");
    if (!carritoItems) return;
    
    carritoItems.innerHTML = "";
    let total = 0;

    if (cart.length === 0) {
      carritoItems.innerHTML = '<p class="text-center text-muted py-3">Tu carrito está vacío</p>';
      if (comprarBtn) comprarBtn.disabled = true;
    } else {
      if (comprarBtn) comprarBtn.disabled = false;

      cart.forEach((item) => {
        total += item.price * item.quantity;
        carritoItems.innerHTML += `
          <div class="cart-item">
            <div class="cart-item-details">
              <div class="cart-item-title">${item.title}</div>
              <div class="cart-item-info">Cantidad: ${item.quantity}</div>
              <div class="cart-item-price">$${(item.price * item.quantity).toFixed(2)}</div>
            </div>
          </div>
        `;
      });
    }

    if (document.getElementById("carrito-total")) {
      document.getElementById("carrito-total").textContent = total.toFixed(2);
    }
    if (document.getElementById("cart-counter")) {
      document.getElementById("cart-counter").textContent = cart.reduce((sum, item) => sum + item.quantity, 0);
    }
    if (document.getElementById("cart-counter-nav")) {
      document.getElementById("cart-counter-nav").textContent = cart.reduce((sum, item) => sum + item.quantity, 0);
    }
  }


  const realizarCompraBtn = document.getElementById("realizar-compra");
  if (realizarCompraBtn) {
    realizarCompraBtn.addEventListener("click", () => {
      const cart = JSON.parse(localStorage.getItem("cart")) || [];
      const pedidoMap = {};
      cart.forEach(item => {
        pedidoMap[item.id] = item.quantity;
      });

      fetch(`${API_URL}/pedidos`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(pedidoMap)
      })
      .then(async (response) => {
const data = await response.json();
if (!response.ok) throw new Error(data.mensaje || "Error en la compra");
return data;
})
.then((pedidoProcesado) => {
if (document.getElementById("modal-total")) {
document.getElementById("modal-total").textContent = pedidoProcesado.costoTotal.toFixed(2);
}
if (typeof bootstrap !== "undefined") {
const modal = new bootstrap.Modal(document.getElementById("compraExitosaModal"));
modal.show();
} else {
alert("¡Compra realizada con éxito! Total pagado: $" + pedidoProcesado.costoTotal.toFixed(2));
}
localStorage.removeItem("cart");
updateCartUI();
obtenerProductos();
})
.catch((error) => {
alert("Operación cancelada:\n" + error.message);
});
});
}
const vaciarCarritoBtn = document.getElementById("vaciar-carrito");
if (vaciarCarritoBtn) {
vaciarCarritoBtn.addEventListener("click", () => {
localStorage.removeItem("cart");
updateCartUI();
});
}

const btnVerPedidos = document.getElementById("btn-ver-pedidos");
if (btnVerPedidos) {
  btnVerPedidos.addEventListener("click", (e) => {
    e.preventDefault();
    const seccionHistorial = document.getElementById("pedidos-historial");
    const historialContainer = document.getElementById("historial-container");
    seccionHistorial.style.display = "block";
    fetch(`${API_URL}/usuarios/5/pedidos`)
      .then((response) => response.json())
      .then((pedidos) => {
        historialContainer.innerHTML = "";
        if (pedidos.length === 0) {
          historialContainer.innerHTML = "Aún no realizaste ningún pedido.";
          return;
        }
        pedidos.forEach((pedido) => {
          const detallesProductos = pedido.lineas
            .map((linea) => `${linea.producto.nombre} (x${linea.cantidad})`)
            .join(", ");
          historialContainer.innerHTML += `
            <div class="col-md-8 mb-3">
              <div class="card w-100 shadow-sm" style="max-width: 100%;">
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-center mb-2">
                    <h5 class="mb-0">Pedido #${pedido.id}</h5>
                    <span class="badge bg-warning text-dark">Estado: ${pedido.estado}</span>
                  </div>
                  <p class="mb-1"><strong>Productos:</strong> ${detallesProductos}</p>
                  <p class="mb-0 text-success"><strong>Costo Total: $${pedido.costoTotal.toFixed(2)}</strong></p>
                </div>
              </div>
            </div>`;
        });
        seccionHistorial.scrollIntoView({ behavior: "smooth" });
      })
      .catch((error) => console.error("Error al cargar historial:", error));
  });
}

const btnSalir = document.getElementById("btn-salir");
if (btnSalir) {
btnSalir.addEventListener("click", (e) => {
e.preventDefault();
localStorage.clear();
alert("Sesión finalizada. El carrito local ha sido limpiado.");
location.reload();
});
}

obtenerProductos();
updateCartUI();
});