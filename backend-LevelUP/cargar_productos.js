const fs = require('fs');

// URL de tu API
const API_URL = 'http://localhost:8080/api/v1/productos';

// Leer el archivo JSON
const rawData = fs.readFileSync('productos_data.json');
const productos = JSON.parse(rawData);

const cargar = async () => {
  console.log(`Iniciando carga de ${productos.length} productos...`);

  for (const producto of productos) {
    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
          // Si tu endpoint requiere auth, agrega aquí: 'Authorization': 'Bearer TU_TOKEN'
        },
        body: JSON.stringify(producto)
      });

      if (response.ok) {
        console.log(`✅ Cargado: ${producto.nombre}`);
      } else {
        console.error(`❌ Error cargando ${producto.nombre}: ${response.status}`);
        const errorText = await response.text();
        console.error('Detalle:', errorText);
      }
    } catch (error) {
      console.error(`❌ Error de red con ${producto.nombre}:`, error.message);
    }
  }
  console.log('--- Proceso finalizado ---');
};

cargar();