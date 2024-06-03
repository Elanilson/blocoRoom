package br.com.apkdoandroid.blocoroom.helper

import android.content.Context
import android.util.Log
import androidx.room.Room
import br.com.apkdoandroid.blocoroom.data.database.BancoDados
import br.com.apkdoandroid.blocoroom.data.entities.Anotacao
import br.com.apkdoandroid.blocoroom.data.entities.Categoria
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

data class BackupData(val categorias: List<Categoria>, val anotacoes: List<Anotacao>)

private var file: File? = null

suspend fun exportarParaJson(context: Context) : File? {

   val job = CoroutineScope(Dispatchers.IO).launch {
        val db = Room.databaseBuilder(context, BancoDados::class.java, Constantes.NOME_BANCO_DADOS).build()
        val categoriaDao = db.categoriaDAO
        val anotacaoDao = db.anotacaoDAO

        val categorias = categoriaDao.listar()
        val anotacoes = anotacaoDao.listar()

        val backupData = BackupData(categorias, anotacoes)

        val gson = Gson()
        val jsonString = gson.toJson(backupData)

         file = File(context.getExternalFilesDir(null), "backup.json")
        try {
            FileWriter(file).use { writer ->
                writer.write(jsonString)
            }
            println("Backup exportado com sucesso: ${file?.absolutePath}")
            Log.d("Backup","Backup exportado com sucesso: ${file?.absolutePath}")

        } catch (e: IOException) {
            Log.e("Backup",e.message.toString())
            e.printStackTrace()
        }
    }

    job.join()

    Log.d("MyDrive", "exportarParaJson - File ${file}")


    return file

}

fun importarDeJson(context: Context) : File? {
    CoroutineScope(Dispatchers.IO).launch {
    val db = Room.databaseBuilder(context, BancoDados::class.java, Constantes.NOME_BANCO_DADOS).build()
    val categoriaDao = db.categoriaDAO
    val anotacaoDao = db.anotacaoDAO

     file = File(context.getExternalFilesDir(null), "backup.json")
    try {
        FileReader(file).use { reader ->
            val gson = Gson()
            val backupDataType = object : TypeToken<BackupData>() {}.type
            val backupData: BackupData = gson.fromJson(reader, backupDataType)

            db.runInTransaction {
                categoriaDao.insertAll(backupData.categorias)
                anotacaoDao.insertAll(backupData.anotacoes)
            }
        }
        println("Backup importado com sucesso")
        Log.d("Backup","Backup importado com sucesso")
    } catch (e: IOException) {
        Log.e("Backup",e.message.toString())
        e.printStackTrace()
    }
    }

    return file
}
