package dominio

import basico.ObjetoBase

class Usuario extends ObjetoBase {
    String nome
    String apelido
    String ip
    Socket socket

    @Override
    public String toString() {
        return "Usuario{" +
                "apelido='" + apelido + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}