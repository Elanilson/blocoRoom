package br.com.apkdoandroid.blocoroom.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.apkdoandroid.blocoroom.data.dao.AnotacaoDAO
import br.com.apkdoandroid.blocoroom.data.dao.CategoriaDAO
import br.com.apkdoandroid.blocoroom.data.entities.Anotacao
import br.com.apkdoandroid.blocoroom.data.entities.Categoria
import br.com.apkdoandroid.blocoroom.helper.Constantes

@Database(
    entities = [Categoria::class, Anotacao::class],
    version = 1
)
abstract class BancoDados : RoomDatabase() {

    //Daos
    abstract val categoriaDAO: CategoriaDAO
    abstract val anotacaoDAO: AnotacaoDAO

    companion object {
        fun getInstance( context: Context ) : BancoDados {
            return Room.databaseBuilder(
                context,
                BancoDados::class.java,
                Constantes.NOME_BANCO_DADOS
            ).build()
        }
    }


}