using System;

namespace BFF.Models
{
    public class Carro
    {
        public long CarroId { get; set; }
        public string UsuarioId { get; set; }
        public long ProductoId { get; set; }
        public long Cantidad { get; set; }
        public DateTime RegistroFecha { get; set; }
        public int VigenciaFlag { get; set; }
    }
} 