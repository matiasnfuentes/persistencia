package ar.edu.unq.eperdemic.services.runner.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.FileInputStream


object FirebaseConnection {

    var dataBase: Firestore

    init {
        val serviceAccount = FileInputStream("./ServiceAccount.json")

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)
        dataBase = FirestoreClient.getFirestore()
    }

}

