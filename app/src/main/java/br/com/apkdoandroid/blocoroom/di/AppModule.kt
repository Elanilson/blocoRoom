package br.com.apkdoandroid.blocoroom.di

import android.content.Context
import androidx.work.WorkManager
import br.com.apkdoandroid.blocoroom.data.dao.AnotacaoDAO
import br.com.apkdoandroid.blocoroom.data.dao.CategoriaDAO
import br.com.apkdoandroid.blocoroom.data.database.BancoDados
import br.com.apkdoandroid.blocoroom.domain.repository.AnotacaoRepository
import br.com.apkdoandroid.blocoroom.data.repository.AnotacaoRepositoryImpl
import br.com.apkdoandroid.blocoroom.domain.repository.CategoriaRepository
import br.com.apkdoandroid.blocoroom.data.repository.CategoriaRepositoryImpl
import br.com.apkdoandroid.blocoroom.domain.repository.ContaGoogleRepository
import br.com.apkdoandroid.blocoroom.data.repository.ContaGoogleRepositoryImpl
import br.com.apkdoandroid.blocoroom.domain.repository.MainRepository
import br.com.apkdoandroid.blocoroom.data.repository.MainRepositoryImpl
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Collections

@Module
@InstallIn( SingletonComponent::class )
object AppModule {

    @Provides
    fun provideBancoDados( @ApplicationContext context: Context ) : BancoDados {
        return BancoDados.getInstance(context)
    }

    @Provides
    fun provideCategoriaDAO( bancoDados: BancoDados ) : CategoriaDAO {
        return bancoDados.categoriaDAO
    }

    @Provides
    fun provideCategoriaRepository( categoriaDAO: CategoriaDAO ) : CategoriaRepository {
        return CategoriaRepositoryImpl( categoriaDAO )
    }

    @Provides
    fun provideAnotacaoDAO( bancoDados: BancoDados ) : AnotacaoDAO {
        return bancoDados.anotacaoDAO
    }

    @Provides
    fun provideAnotacaoRepository( anotacaoDAO: AnotacaoDAO ) : AnotacaoRepository {
        return AnotacaoRepositoryImpl( anotacaoDAO )
    }

    @Provides
    fun provideContaGoogleRepository(bancoDados: BancoDados,googleDrive: Drive?,googleSignInClient: GoogleSignInClient) : ContaGoogleRepository {
        return ContaGoogleRepositoryImpl(bancoDados,googleDrive,googleSignInClient)
    }

    @Provides
    fun provideMainRepository(bancoDados: BancoDados,googleDrive: Drive?,googleSignInClient: GoogleSignInClient) : MainRepository {
        return MainRepositoryImpl(bancoDados,googleDrive,googleSignInClient)
    }

    @Provides
    fun provideGoogleSignInOptions() : GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
    }
    @Provides
    fun provideGoogleSignInClient( @ApplicationContext context: Context, googleSignInOptions: GoogleSignInOptions) : GoogleSignInClient{
        return GoogleSignIn.getClient(context,googleSignInOptions)
    }

    @Provides
    fun provideGoogleAccountCredential(@ApplicationContext context: Context) :  GoogleAccountCredential?{
        val mAccount = GoogleSignIn.getLastSignedInAccount(context)
        val credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_FILE))
        credential?.selectedAccount = mAccount?.account
        return credential
    }

    @Provides
    fun provideDriveService(credential: GoogleAccountCredential?) : Drive?{
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory(),
            credential
        ).setApplicationName("BlocoRoom").build()
    }



}