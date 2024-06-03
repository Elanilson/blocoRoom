package br.com.apkdoandroid.blocoroom.presentation.model

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

data class Resposta(
    var status: Boolean = false,
    var mensagem: String? = null,
    var googleSignInAccount: GoogleSignInAccount? = null
) {
    companion object {
        fun onSuccess(mensagem: String): Resposta {
            return Resposta(true, mensagem)
        }

      /*  fun onSuccess(googleSignInAccount: GoogleSignInAccount): Resposta {
            return Resposta(true, null,googleSignInAccount)
        }*/

        fun onFailure(mensagem: String): Resposta {
            return Resposta(false, mensagem)
        }
    }

    // Novo construtor que aceita apenas o parâmetro "mensagem"
    constructor(mensagem: String) : this(false, mensagem)
    constructor(status: Boolean ) : this(status,null)
    constructor(googleSignInAccount: GoogleSignInAccount?,status: Boolean) : this(status, null,googleSignInAccount)
}