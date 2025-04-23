package cat.itb.dam.m78.camerademo.camera

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform