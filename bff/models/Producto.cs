using System;

namespace BFF.Models
{
    public class Producto
    {
        public long ProductoId { get; set; }
        public string Nombre { get; set; }
        public string Descripcion { get; set; }
        public long StockActual { get; set; }
        public DateTime RegistroFecha { get; set; }
        public long ValorCosto { get; set; }
        public long ValorVenta { get; set; }
        public string ImagenUri { get; set; }
    }
} 