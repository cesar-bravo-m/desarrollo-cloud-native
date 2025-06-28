using System;

namespace BFF.Models
{
    public class Usuario
    {
        public long UsuarioId { get; set; }
        public string Email { get; set; }
        public string Nombre { get; set; }
        public DateTime LoginUltimo { get; set; }
    }
} 