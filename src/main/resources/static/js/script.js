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

    cardAnimation();

});


// ==========================
// 開始・終了時間作成
// ==========================

function createTimeOptions(id){

    const select = document.getElementById(id);

    if(!select){
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

    clock.textContent =
        `${yyyy}/${mm}/${dd} ${hh}:${mi}:${ss}`;

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

function showModal(){

    const modal =
        document.getElementById("modal");


    if(!modal){
        return;
    }


    modal.style.display="block";


    setTimeout(function(){

        modal.classList.add("show");

    },10);

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


    setTimeout(function(){

        modal.style.display="none";

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

    fetch("/schedule?date=" + date)

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

            card.innerHTML = `
                <strong>
                    ${work.startTime} ～ ${work.endTime}
                </strong>

                <p>🚜 ${work.workType ?? ""}</p>

                <p>👤 ${work.userName ?? ""}</p>

                <p>🌱 ${work.cropName ?? ""}</p>

                <p>📍 ${work.fieldName ?? ""}</p>
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

    fetch("/schedule/edit?id=" + id)

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


document.addEventListener("click",function(e){


    // 作業カードクリック
    const card =
        e.target.closest(".schedule-card");


    if(card){


        e.stopPropagation();


        const id =
            card.dataset.id;


        if(id){

            editSchedule(id);

        }


        return;

    }
	// ==========================
	// 編集保存
	// ==========================

	function editSave(){

	    const form =
	        document.getElementById("scheduleForm");


	    form.action="/update";


	    form.submit();

	}


    // 日付枠クリック

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
// 作業カードクリック
// ==========================
// ========================================
// 日付・作業カードクリック統合
// ========================================

document.addEventListener("click", function(e){


    // ==========================
    // 作業カードクリック
    // ==========================

    const card =
        e.target.closest(".schedule-card");


    if(card){


        // 日付クリックを止める
        e.stopPropagation();


        const id =
            card.dataset.id;


        if(id){

            editSchedule(id);

        }


        return;

    }



    // ==========================
    // 日付枠クリック
    // ==========================

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
