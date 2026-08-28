// ========================================
// calendar.js
// 農業管理システム
// Part 1 / 6
// ========================================


// ==========================
// ページ読み込み
// ==========================

window.addEventListener("DOMContentLoaded", function () {

    createTimeOptions("startTime");
    createTimeOptions("endTime");

    updateClock();
    setInterval(updateClock, 1000);

    setupWeatherButton();

    cardAnimation();

});


// ==========================
// 開始・終了時間作成
// ==========================

function createTimeOptions(id){

    const select = document.getElementById(id);

    if(!select || select.tagName !== "SELECT"){
        return;
    }

    // HTMLにoptionがある場合は追加しない
    if(select.options.length > 0){
        return;
    }

    for(let hour = 5; hour <= 20; hour++){

        for(const minute of [0,30]){

            const option =
                document.createElement("option");

            const time =
                String(hour).padStart(2,"0")
                + ":"
                + String(minute).padStart(2,"0");

            option.value = time;
            option.textContent = time;

            select.appendChild(option);

        }

    }

}

// null・空文字を画面に出さないための共通処理
function displayValue(value, fallback){

    if(value === null || value === undefined || String(value).trim() === ""){
        return fallback;
    }

    return String(value);
}

// APIから取得した文字列をHTMLへ安全に表示する
function escapeHtml(value){

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}



// ==========================
// 時計表示
// ==========================

function updateClock(){

    const clock =
        document.getElementById("clock");

    if(!clock){
        return;
    }

    const now = new Date();

    const yyyy = now.getFullYear();

    const mm =
        String(now.getMonth()+1).padStart(2,"0");

    const dd =
        String(now.getDate()).padStart(2,"0");

    const hh =
        String(now.getHours()).padStart(2,"0");

    const mi =
        String(now.getMinutes()).padStart(2,"0");

    const ss =
        String(now.getSeconds()).padStart(2,"0");

		clock.innerHTML =
		`
		<span>${yyyy}/${mm}/${dd}</span>
		<span>${hh}:${mi}:${ss}</span>
		`;
}

function setupWeatherButton(){

    const button =
        document.getElementById("weatherButton");

    if(!button){
        return;
    }

    button.addEventListener("click", requestCurrentWeather);
}

function requestCurrentWeather(){

    const button =
        document.getElementById("weatherButton");

    const label =
        document.getElementById("weatherButtonLabel");

    if(!button || !label){
        return;
    }

    if(!navigator.geolocation){
        showWeatherMessage(
            "この端末では現在地を取得できません。",
            true
        );
        label.textContent = "もう一度試す";
        return;
    }

    button.disabled = true;
    label.textContent = "現在地を確認中";
    showWeatherMessage(
        "位置情報の許可を確認しています…",
        false
    );

    navigator.geolocation.getCurrentPosition(
        function(position){
            fetchCurrentWeather(
                position.coords.latitude,
                position.coords.longitude
            );
        },
        function(error){
            button.disabled = false;
            label.textContent = "もう一度試す";
            showWeatherMessage(
                getGeolocationErrorMessage(error),
                true
            );
        },
        {
            enableHighAccuracy: false,
            timeout: 12000,
            maximumAge: 600000
        }
    );
}

async function fetchCurrentWeather(latitude, longitude){

    const button =
        document.getElementById("weatherButton");

    const label =
        document.getElementById("weatherButtonLabel");

    try{
        label.textContent = "天気を取得中";
        showWeatherMessage(
            "現在地の天気を取得しています…",
            false
        );

        const params =
            new URLSearchParams({
                latitude: String(latitude),
                longitude: String(longitude),
                current: [
                    "temperature_2m",
                    "apparent_temperature",
                    "relative_humidity_2m",
                    "precipitation",
                    "weather_code",
                    "wind_speed_10m"
                ].join(","),
                daily: [
                    "weather_code",
                    "temperature_2m_max",
                    "temperature_2m_min",
                    "precipitation_probability_max"
                ].join(","),
                timezone: "auto",
                forecast_days: "3"
            });

        const response =
            await fetch(
                "https://api.open-meteo.com/v1/forecast?"
                + params.toString(),
                {
                    headers: {
                        "Accept": "application/json"
                    }
                }
            );

        if(!response.ok){
            throw new Error(
                "Weather API returned " + response.status
            );
        }

        const data =
            await response.json();

        if(!data.current){
            throw new Error("Current weather is missing");
        }

        renderWeather(data);

        label.textContent = "天気を更新";
        button.setAttribute("aria-expanded", "true");

    }catch(error){
        console.error("Weather Error:", error);

        label.textContent = "もう一度試す";
        showWeatherMessage(
            "天気情報を取得できませんでした。通信状況を確認してください。",
            true
        );

    }finally{
        button.disabled = false;
    }
}

function renderWeather(data){

    const result =
        document.getElementById("weatherResult");

    const currentElement =
        document.getElementById("weatherCurrent");

    const detailsElement =
        document.getElementById("weatherDetails");

    const forecastElement =
        document.getElementById("weatherForecast");

    if(
        !result
        || !currentElement
        || !detailsElement
        || !forecastElement
    ){
        return;
    }

    const current = data.current;
    const weather =
        getWeatherCodeInfo(current.weather_code);

    result.hidden = false;
    result.classList.remove("is-error");

    currentElement.textContent =
        weather.icon
        + " 現在地 "
        + weather.label
        + " "
        + formatWeatherNumber(
            current.temperature_2m,
            0
        )
        + "℃";

    detailsElement.textContent =
        "体感 "
        + formatWeatherNumber(
            current.apparent_temperature,
            0
        )
        + "℃・湿度 "
        + formatWeatherNumber(
            current.relative_humidity_2m,
            0
        )
        + "%・雨量 "
        + formatWeatherNumber(
            current.precipitation,
            1
        )
        + "mm・風速 "
        + formatWeatherNumber(
            current.wind_speed_10m,
            1
        )
        + "km/h";

    forecastElement.replaceChildren();

    const daily = data.daily;

    if(!daily || !Array.isArray(daily.time)){
        return;
    }

    const dayCount =
        Math.min(3, daily.time.length);

    for(let index = 0; index < dayCount; index++){

        const item =
            document.createElement("article");

        item.className = "weather-forecast-item";

        const date =
            document.createElement("p");

        date.className = "weather-forecast-date";
        date.textContent =
            formatWeatherDate(daily.time[index]);

        const weatherInfo =
            getWeatherCodeInfo(
                daily.weather_code?.[index]
            );

        const condition =
            document.createElement("p");

        condition.className =
            "weather-forecast-condition";

        condition.textContent =
            weatherInfo.icon
            + " "
            + weatherInfo.label;

        const temperature =
            document.createElement("p");

        temperature.className =
            "weather-forecast-temperature";

        temperature.textContent =
            formatWeatherNumber(
                daily.temperature_2m_max?.[index],
                0
            )
            + "℃ / "
            + formatWeatherNumber(
                daily.temperature_2m_min?.[index],
                0
            )
            + "℃";

        const rain =
            document.createElement("p");

        rain.className =
            "weather-forecast-rain";

        rain.textContent =
            "降水 "
            + formatWeatherNumber(
                daily.precipitation_probability_max?.[index],
                0
            )
            + "%";

        item.append(
            date,
            condition,
            temperature,
            rain
        );

        forecastElement.appendChild(item);
    }
}

function showWeatherMessage(message, isError){

    const result =
        document.getElementById("weatherResult");

    const currentElement =
        document.getElementById("weatherCurrent");

    const detailsElement =
        document.getElementById("weatherDetails");

    const forecastElement =
        document.getElementById("weatherForecast");

    if(
        !result
        || !currentElement
        || !detailsElement
        || !forecastElement
    ){
        return;
    }

    result.hidden = false;
    result.classList.toggle(
        "is-error",
        Boolean(isError)
    );

    currentElement.textContent = message;
    detailsElement.textContent = "";
    forecastElement.replaceChildren();
}

function getGeolocationErrorMessage(error){

    if(error && error.code === 1){
        return "位置情報が許可されていません。ブラウザの設定から位置情報を許可してください。";
    }

    if(error && error.code === 2){
        return "現在地を取得できませんでした。GPSまたは通信状況を確認してください。";
    }

    if(error && error.code === 3){
        return "現在地の取得がタイムアウトしました。もう一度お試しください。";
    }

    return "現在地を取得できませんでした。";
}

function getWeatherCodeInfo(code){

    const value = Number(code);

    if(value === 0){
        return { icon: "☀️", label: "快晴" };
    }

    if([1, 2].includes(value)){
        return { icon: "🌤️", label: "晴れ" };
    }

    if(value === 3){
        return { icon: "☁️", label: "くもり" };
    }

    if([45, 48].includes(value)){
        return { icon: "🌫️", label: "霧" };
    }

    if([51, 53, 55, 56, 57].includes(value)){
        return { icon: "🌦️", label: "霧雨" };
    }

    if([61, 63, 65, 66, 67].includes(value)){
        return { icon: "🌧️", label: "雨" };
    }

    if([71, 73, 75, 77, 85, 86].includes(value)){
        return { icon: "🌨️", label: "雪" };
    }

    if([80, 81, 82].includes(value)){
        return { icon: "🌦️", label: "にわか雨" };
    }

    if([95, 96, 99].includes(value)){
        return { icon: "⛈️", label: "雷雨" };
    }

    return { icon: "🌤️", label: "天気不明" };
}

function formatWeatherNumber(value, digits){

    const number = Number(value);

    if(!Number.isFinite(number)){
        return "—";
    }

    return number.toFixed(digits);
}

function formatWeatherDate(value){

    const date =
        new Date(String(value) + "T00:00:00");

    if(Number.isNaN(date.getTime())){
        return String(value);
    }

    const weekdays =
        ["日", "月", "火", "水", "木", "金", "土"];

    return (
        (date.getMonth() + 1)
        + "/"
        + date.getDate()
        + "（"
        + weekdays[date.getDay()]
        + "）"
    );
}


// ==========================
// カードアニメーション
// ==========================

function cardAnimation(){

    const cards =
        document.querySelectorAll(
            ".summary-card,.status-card,.card"
        );

    cards.forEach(function(card,index){

        card.style.opacity = "0";
        card.style.transform =
            "translateY(30px)";

        setTimeout(function(){

            card.style.transition =
                ".45s";

            card.style.opacity = "1";

            card.style.transform =
                "translateY(0)";

        },index * 100);

    });

}



// ========================================
// Part2
// モーダル処理
// ========================================


// ==========================
// モーダル表示
// ==========================

let modalCloseTimer = null;

function showModal(){

    const modal =
        document.getElementById("modal");


    if(!modal){
        return;
    }


    if(modalCloseTimer){
        clearTimeout(modalCloseTimer);
        modalCloseTimer = null;
    }

    modal.style.display="flex";
    modal.setAttribute("aria-hidden", "false");
    document.body.classList.add("modal-open");

    const modalContent = modal.querySelector(".modal-content");
    if(modalContent){
        modalContent.scrollTop = 0;
    }


    requestAnimationFrame(function(){

        modal.classList.add("show");

    });

}




// ==========================
// モーダル閉じる
// ==========================

function closeModal(){

    const modal =
        document.getElementById("modal");


    if(!modal){
        return;
    }


    modal.classList.remove("show");
    modal.setAttribute("aria-hidden", "true");
    document.body.classList.remove("modal-open");


    modalCloseTimer = setTimeout(function(){

        modal.style.display="none";
        modalCloseTimer = null;

    },300);

}




// ==========================
// 背景クリック
// ==========================

window.addEventListener("click",function(event){


    const modal =
        document.getElementById("modal");


    if(event.target === modal){

        closeModal();

    }


});



// ==========================
// ESC
// ==========================

window.addEventListener("keydown",function(event){

    if(event.key==="Escape"){

        closeModal();

    }

});








// ==========================
// ESCキーで閉じる
// ==========================




// ==========================
// 新規登録画面を開く
// ==========================
function openAddDay(date){


    // 入力欄リセット

    document
    .getElementById("scheduleForm")
    .reset();



    // IDクリア
    document
    .getElementById("scheduleId")
    .value="";



    // 登録モード

    document
    .getElementById("saveBtn")
    .style.display="inline-block";



    document
    .getElementById("updateBtn")
    .style.display="none";



    document
    .getElementById("deleteBtn")
    .style.display="none";



    document
    .getElementById("scheduleForm")
    .action="/save";



    document
    .getElementById("date")
    .value=date;



    document
    .getElementById("selectedDate")
    .textContent=date;



    showModal();



    // 同じ日の作業一覧表示

    loadScheduleList(date);

}

// ========================================
// calendar.js
// Part 3 / 6
// 作業一覧表示
// ========================================


// ==========================
// 指定日の作業一覧取得
// ==========================

function loadScheduleList(date){

    const list =
        document.getElementById("scheduleList");

    if(!list){
        return;
    }

    list.innerHTML =
        "<p>読み込み中...</p>";

    fetch("/schedule?date=" + encodeURIComponent(date))

    .then(response => {

        if(!response.ok){

            throw new Error("取得失敗");

        }

        return response.json();

    })

    .then(data => {

        list.innerHTML = "";

        if(data.length === 0){

            list.innerHTML =
                "<p>登録済み作業はありません。</p>";

            return;

        }

        data.forEach(work => {

            const card =
                document.createElement("div");

            card.className =
                "schedule-history-card";

            const startTime = escapeHtml(displayValue(work.startTime, "--:--"));
            const endTime = escapeHtml(displayValue(work.endTime, "--:--"));
            const workType = escapeHtml(displayValue(work.workType, "作業種類未設定"));
            const userName = escapeHtml(displayValue(work.userName, "担当者未設定"));
            const cropName = escapeHtml(displayValue(work.cropName, "作物未設定"));
            const fieldName = escapeHtml(displayValue(work.fieldName, "圃場未設定"));
            const schedule = escapeHtml(displayValue(work.schedule, "作業内容未設定"));

            card.innerHTML = `
                <strong>
                    ${startTime} ～ ${endTime}
                </strong>

                <p>🚜 ${workType}</p>

                <p>📝 ${schedule}</p>

                <p>👤 ${userName}</p>

                <p>🌱 ${cropName}</p>

                <p>📍 ${fieldName}</p>
            `;

            // カード全体クリックで編集
            card.addEventListener("click",function(){

                editSchedule(work.id);

            });

            list.appendChild(card);

        });

    })

    .catch(error => {

        console.error(error);

        list.innerHTML =
            "<p>作業一覧の取得に失敗しました。</p>";

    });

}
// ========================================
// calendar.js
// Part 4 / 6
// 編集処理
// ========================================

// ==========================
// 編集画面を開く
// ==========================

function editSchedule(id){

    fetch("/schedule/edit?id=" + encodeURIComponent(id))

    .then(response => {

        if(!response.ok){

            throw new Error("取得失敗");

        }

        return response.json();

    })


    .then(work => {


        showModal();



        // ==========================
        // 編集モード
        // ==========================


        document
        .getElementById("scheduleForm")
        .action="/update";



        // 登録ボタン非表示

        document
        .getElementById("saveBtn")
        .style.display="none";



        // 更新ボタン表示

        document
        .getElementById("updateBtn")
        .style.display="inline-block";



        // 消去ボタン表示

        document
        .getElementById("deleteBtn")
        .style.display="inline-block";





        // ==========================
        // ID
        // ==========================

        document
        .getElementById("scheduleId")
        .value =
        work.id;





        // ==========================
        // 日付
        // ==========================

        document
        .getElementById("date")
        .value =
        work.date;


        document
        .getElementById("selectedDate")
        .textContent =
        work.date;





        // ==========================
        // 時間
        // ==========================

        document
        .getElementById("startTime")
        .value =
        work.startTime;


        document
        .getElementById("endTime")
        .value =
        work.endTime;





        // ==========================
        // 担当者
        // ==========================

        document
        .getElementById("userName")
        .value =
        work.userName ?? "";





        // ==========================
        // 作業内容
        // ==========================

        document
        .getElementById("schedule")
        .value =
        work.schedule ?? "";





        // ==========================
        // 圃場
        // ==========================

        document
        .getElementById("fieldName")
        .value =
        work.fieldName ?? "";





        // ==========================
        // 作物
        // ==========================

        document
        .getElementById("cropName")
        .value =
        work.cropName ?? "";





        // ==========================
        // 作業種類
        // ==========================

        document
        .getElementById("workType")
        .value =
        work.workType ?? "";





        // ==========================
        // メモ
        // ==========================

        document
        .getElementById("memo")
        .value =
        work.memo ?? "";



    })


    .catch(error => {


        console.error(error);


        alert("編集データの取得に失敗しました。");


    });


}
// ========================================
// calendar.js
// Part 5 / 6
// 削除処理
// ========================================

// ==========================
// 作業削除
// ==========================

function deleteSchedule(){

    const id =
        document.getElementById("scheduleId").value;

    if(!id){

        alert("削除する作業を選択してください。");

        return;

    }

    if(!confirm("この作業を削除しますか？")){

        return;

    }

    fetch("/delete/" + id, {

        method:"POST"

    })

    .then(response => {

        if(!response.ok){

            throw new Error("削除失敗");

        }

        alert("削除しました。");

        location.reload();

    })

    .catch(error => {

        console.error(error);

        alert("削除できませんでした。");

    });

}
// ========================================
// calendar.js
// Part 6 / 6
// イベント処理
// ========================================

// ==========================
// 日付セルクリック
// ==========================

// ========================================
// Part6
// 日付・作業カードクリック
// ========================================


// ==========================
// 作業カードクリック
// ==========================
// ========================================
// 日付・作業カードクリック統合
// ========================================
document.addEventListener("click", function(e){


    // ==========================
    // サイドバーリンク除外
    // ==========================

    if(e.target.closest(".menu a")){
        return;
    }


    const card =
        e.target.closest(".schedule-card");


    if(card){


        const id =
            card.dataset.id;

        if(id){

            editSchedule(id);

        }

        return;

    }


    const dayBox =
        e.target.closest(".day-box");


    if(dayBox){

        const date =
            dayBox.dataset.date;

        if(date){

            openAddDay(date);

        }

    }


});

// ==========================
// PWA Service Worker
// ==========================

if ("serviceWorker" in navigator) {

    window.addEventListener("load", function(){

		navigator.serviceWorker.register("/service-worker.js?v=20260829-1")
        .then(function(registration){

            console.log("PWA Ready");
            registration.update();

        })
        .catch(function(error){

            console.log("PWA Error:", error);

        });

    });

}
// ==========================
// スマホ サイドバー開閉
// ==========================

document.addEventListener("DOMContentLoaded", function(){

    const menuBtn =
        document.getElementById("menuBtn");


    const sidebar =
        document.querySelector(".sidebar");


    const main =
        document.querySelector(".main-content");


    if(menuBtn && sidebar){


        menuBtn.addEventListener("click", function(){


            sidebar.classList.toggle("active");

            menuBtn.setAttribute(
                "aria-expanded",
                sidebar.classList.contains("active") ? "true" : "false"
            );


        });


    }


    // 右側の余白クリックで閉じる

    if(main && sidebar){


        main.addEventListener("click", function(){


            sidebar.classList.remove("active");

            if(menuBtn){
                menuBtn.setAttribute("aria-expanded", "false");
            }


        });


    }


});
document.addEventListener("DOMContentLoaded", function () {

    const addButtons =
        document.querySelectorAll(".add-schedule-button");

    addButtons.forEach(function (button) {

        button.addEventListener("click", function (event) {

            event.stopPropagation();

            const dayBox = button.closest(".day-box");
            const date = button.dataset.date ||
                (dayBox ? dayBox.dataset.date : "");

            if(date){
                openAddDay(date);
            }
        });
    });
});
