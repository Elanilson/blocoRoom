package br.com.apkdoandroid.blocoroom.domain.repository

import android.content.Context
import android.content.Intent
import br.com.apkdoandroid.blocoroom.helper.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.services.drive.Drive

interface ContaGoogleRepository {
    suspend fun login() : Intent
    suspend fun logout() : Resource<Boolean>
    suspend fun verificarUsuarioLogdo(context: Context) :  Resource<GoogleSignInAccount?>
    suspend fun upload(localFile: java.io.File, mimeType: String?, folderId: String?) : Resource<Boolean>
    suspend fun download(folderName: String, fileName: String, targetFile: java.io.File) :Resource<Boolean>

    suspend fun importarBancoDados(context: Context) : Resource<Boolean>
    suspend fun exportarBancoDados(context: Context) : Resource<Boolean>

}