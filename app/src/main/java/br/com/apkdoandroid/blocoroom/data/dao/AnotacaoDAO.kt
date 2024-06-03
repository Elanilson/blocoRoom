package br.com.apkdoandroid.blocoroom.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.apkdoandroid.blocoroom.data.entities.Anotacao
import br.com.apkdoandroid.blocoroom.data.entities.Categoria
import br.com.apkdoandroid.blocoroom.data.entities.relacionamentos.AnotacaoECategoria

@Dao
interface AnotacaoDAO {

    @Insert
    fun salvar( anotacao: Anotacao ) : Long

    @Delete
    fun remover( anotacao: Anotacao ) : Int

    @Update
    fun atualizar( anotacao: Anotacao ) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(anotacoes: List<Anotacao>)

    @Query("SELECT * FROM anotacoes")
    fun listar() : List<Anotacao>

    @Query("SELECT * FROM anotacoes")
    fun listarAnotacaoECategoria() : List<AnotacaoECategoria>

    @Query("SELECT * FROM anotacoes a " +//me
            "WHERE a.titulo LIKE '%' || :texto || '%' " +
            "OR a.descricao LIKE '%' || :texto || '%'  ")
    fun pesquisarAnotacaoECategoria( texto: String ) : List<AnotacaoECategoria>

}