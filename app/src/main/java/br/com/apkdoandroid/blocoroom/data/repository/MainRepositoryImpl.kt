package br.com.apkdoandroid.blocoroom.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import br.com.apkdoandroid.blocoroom.data.database.BancoDados
import br.com.apkdoandroid.blocoroom.domain.repository.MainRepository
import br.com.apkdoandroid.blocoroom.helper.BackupData
import br.com.apkdoandroid.blocoroom.helper.Resource

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import javax.inject.Inject

/**
 * Para uma abordagem limpa e seguindo as melhores práticas de MVVM e Clean Architecture,
 * você deve manter a lógica de negócio e operações assíncronas fora do ViewModel sempre que possível.
 * O ViewModel deve orquestrar chamadas ao repositório, que lida com a lógica específica do domínio
 * (como a execução de um backup).
 */
class MainRepositoryImpl @Inject constructor(
    private val bancoDados: BancoDados,
    private val driveService: Drive?,
    private val googleSignInClient: GoogleSignInClient
) : MainRepository {

    override suspend fun login(): Intent {
        return googleSignInClient.signInIntent
        // return Intent()
    }

    override suspend fun logout(): Resource<Boolean> {
        var resource : Resource<Boolean>

        if (googleSignInClient != null) {
            googleSignInClient.signOut()
                .addOnCompleteListener {
                    Log.d("MyDrive", "Logout bem-sucedido")
                    resource = Resource.Success(true)
                }.addOnFailureListener {
                    Log.d("MyDrive", "Logout failure ${it.message}")
                    resource =  Resource.Error(it.message.toString(), null)
                }
        }

        resource = Resource.Error("googleSignInClient está null", null)

        return resource

    }

    override suspend fun verificarUsuarioLogdo(context: Context): Resource<GoogleSignInAccount?> {
        val account = GoogleSignIn.getLastSignedInAccount(context)

       return if(account != null){
            Log.d("MyDrive", "Usuário  está logado")
            Resource.Success(account)
        }else{
            Log.d("MyDrive", "Usuário não está logado")
            Resource.Error("Usuário não está logado",null)
        }

    }

    override suspend fun upload(localFile: java.io.File, mimeType: String?, folderId: String?): Resource<Boolean> {
        Log.d("MyDrive", "Entrei no upload")
      return  withContext(Dispatchers.IO) {


            try {
                if (driveService != null) {
                    // Verificar se a pasta existe ou precisa ser criada
                    val finalFolderId =
                        folderId?.let { createFolderIfNotExists(it, driveService) } ?: "root"

                    // Verificar se o arquivo já existe
                    val existingFileId =
                        getFileIdByName(finalFolderId, localFile.name, driveService)

                    val metadata = com.google.api.services.drive.model.File().apply {
                        this.mimeType = mimeType
                        name = localFile.name
                    }
                    val fileContent = com.google.api.client.http.FileContent(mimeType, localFile)

                    val fileMeta = if (existingFileId != null) {
                        // Atualizar o arquivo existente
                        driveService.files().update(existingFileId, metadata, fileContent).execute()
                    } else {
                        // Criar um novo arquivo
                        metadata.parents = listOf(finalFolderId)
                        driveService.files().create(metadata, fileContent).execute()
                    }

                    if (fileMeta != null) {
                        // Sucesso no upload
                        Log.d("MyDrive", "Sucesso no upload ${fileMeta}")
                        Resource.Success(true)
                    } else {
                        // Falha no upload
                        Log.d("MyDrive", "Falha no upload")
                        Resource.Error("Falha no upload",null)
                    }
                } else {
                    Log.d("MyDrive", "Drive está null")
                    Resource.Error("Drive está null",null)
                }
            } catch (e: Exception) {
                Log.d("MyDrive", "Erro Upload ${e.message}")
                Resource.Error(e.message.toString(),null)
            }
        }
    }

    override suspend fun download(folderName: String, fileName: String, targetFile: java.io.File): Resource<Boolean> {
        return withContext(Dispatchers.IO) {

            try {
                val folderId = getFolderIdByName(folderName,driveService)
                if (folderId != null) {
                    val fileId = getFileIdByName(folderId, fileName,driveService)
                    if (fileId != null) {
                        downloadFile(fileId, targetFile,driveService)
                        Resource.Success(true)
                    } else {
                        println("Arquivo não encontrado")
                        Log.d("MyDrive", "Arquivo não encontrado")
                        Resource.Error("Arquivo não encontrado",null)
                    }
                } else {
                    println("Pasta não encontrada")
                    Log.d("MyDrive", "Pasta não encontrada")
                    Resource.Error("Pasta não encontrada",null)
                }
            } catch (e: Exception) {
                Log.e("MyDrive", e.message.toString())
                e.printStackTrace()
                Resource.Error(e.message.toString(),null)
            }
        }
    }

    override suspend fun importarBancoDados(context: Context): Resource<Boolean> {
        return withContext(Dispatchers.IO) {

            val categoriaDao = bancoDados.categoriaDAO
            val anotacaoDao = bancoDados.anotacaoDAO

           val file = java.io.File(context.getExternalFilesDir(null), "backup.json")
            try {

                FileReader(file).use { reader ->
                    val gson = Gson()
                    val backupDataType = object : TypeToken<BackupData>() {}.type
                    val backupData: BackupData = gson.fromJson(reader, backupDataType)

                    bancoDados.runInTransaction {
                        categoriaDao.insertAll(backupData.categorias)
                        anotacaoDao.insertAll(backupData.anotacoes)
                    }
                }
                println("Backup importado com sucesso")
                Log.d("Backup","Backup importado com sucesso")
                Resource.Success(true)
            } catch (e: IOException) {
                Log.e("Backup",e.message.toString())
                e.printStackTrace()
                Resource.Error(e.message.toString(),null)
            }
        }
    }

    override suspend fun exportarBancoDados(context: Context): Resource<Boolean> {
        return withContext(Dispatchers.IO) {

         //   val job = CoroutineScope(Dispatchers.IO).launch {
            val categoriaDao = bancoDados.categoriaDAO
            val anotacaoDao = bancoDados.anotacaoDAO

                val categorias = categoriaDao.listar()
                val anotacoes = anotacaoDao.listar()

                val backupData = BackupData(categorias, anotacoes)

                val gson = Gson()
                val jsonString = gson.toJson(backupData)

               val file = java.io.File(context.getExternalFilesDir(null), "backup.json")

                try {

                    FileWriter(file).use { writer ->
                        writer.write(jsonString)
                    }
                    println("Backup exportado com sucesso: ${file?.absolutePath}")
                    Log.d("Backup","Backup exportado com sucesso: ${file?.absolutePath}")
                    Resource.Success(true)
                } catch (e: IOException) {
                    Log.e("Backup",e.message.toString())
                    e.printStackTrace()
                    Resource.Error(e.message.toString(),null)
                }
        //    }

        //    job.join()

          //  Log.d("MyDrive", "exportarParaJson - File ${file}")
        }
    }


    private suspend fun createFolderIfNotExists(folderName: String, driveService: Drive?): String? {
        return withContext(Dispatchers.IO) {

            Log.d("MyDrive", " folderName ${folderName}")
            try {
                // Procurar a pasta pelo nome
                val query = "mimeType='application/vnd.google-apps.folder' and name='$folderName' and trashed=false"
                val result = driveService?.files()?.list()?.setQ(query)?.setSpaces("drive")?.execute()
                val folders = result?.files

                if (folders.isNullOrEmpty()) {
                    // Pasta não encontrada, criar uma nova pasta
                    val metadata = File().apply {
                        mimeType = "application/vnd.google-apps.folder"
                       // name = folderName
                        name = "anotacoes"
                        parents = listOf("root") // Ou outra pasta pai se necessário
                    }
                    val folderMeta = driveService?.files()?.create(metadata)?.execute()
                    Log.d("MyDrive", "Pasta criada: ${folderMeta}")
                    folderMeta?.id
                } else {
                    Log.d("MyDrive", " else ${folders.first().id}")
                    // Retornar o ID da pasta encontrada
                    folders.first().id
                }
            } catch (e: Exception) {
                Log.d("MyDrive", "Erro createFolderIfNotExists ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }

   private suspend fun getFolderIdByName(folderName: String, driveService: Drive?): String? {
        return withContext(Dispatchers.IO) {
            try {
                val query = "mimeType = 'application/vnd.google-apps.folder' and name = '$folderName' and trashed = false"
                val result = driveService?.files()?.list()?.setQ(query)?.setSpaces("drive")?.execute()
                val folder = result?.files?.firstOrNull()
                folder?.id
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

   private suspend fun getFileIdByName(folderId: String, fileName: String, driveService: Drive?): String? {
        return withContext(Dispatchers.IO) {
            try {
                val query = "'$folderId' in parents and name = '$fileName' and trashed = false"
                val result = driveService?.files()?.list()?.setQ(query)?.setSpaces("drive")?.execute()
                val file = result?.files?.firstOrNull()
                file?.id
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private suspend fun downloadFile(fileId: String, targetFile: java.io.File,driveService: Drive?): Boolean {
        return withContext(Dispatchers.IO) {

            try {
                Log.d("MyDrive", "file recebido ${targetFile}")
                val outputStream = targetFile.outputStream()
                driveService?.files()?.get(fileId)?.executeMediaAndDownloadTo(outputStream)
                outputStream.flush()
                outputStream.close()
                Log.d("MyDrive", "downloadFile ${outputStream}")
                true
            } catch (e: Exception) {
                Log.d("MyDrive", "Erro downloadFile ${e.message}")
                e.printStackTrace()
                false
            }
        }
    }

}