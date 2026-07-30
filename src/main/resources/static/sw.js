// ========================================
// 農業管理システム
// Service Worker
// ========================================

const CACHE_NAME = "agriculture-system-v1";


const CACHE_FILES = [
    "/",
    "/manifest.json",
    "/css/style.css",
    "/icons/icon-192.png",
    "/icons/icon-512.png"
];


// インストール
self.addEventListener("install", function(event){

    event.waitUntil(

        caches.open(CACHE_NAME)
        .then(function(cache){

            return cache.addAll(CACHE_FILES);

        })

    );

    self.skipWaiting();

});



// 有効化
self.addEventListener("activate", function(event){

    event.waitUntil(

        self.clients.claim()

    );

});



// キャッシュ取得
self.addEventListener("fetch", function(event){

    event.respondWith(

        caches.match(event.request)
        .then(function(response){

            return response || fetch(event.request);

        })

    );

});