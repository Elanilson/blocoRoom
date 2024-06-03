package br.com.apkdoandroid.blocoroom.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = "anotacoes",
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id_categoria"],
            childColumns = ["id_categoria"]
        )
    ]
)
@Parcelize
data class Anotacao(
    @PrimaryKey( autoGenerate = true )
    @ColumnInfo( name = "id_anotacao" )
    val idAnotacao: Long,
    @ColumnInfo( name = "id_categoria" )
    val idCategoria: Long,
    val titulo: String,
    val descricao: String
) : Parcelable
