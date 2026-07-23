// ==========================
// ページ読み込み
// ==========================

window.addEventListener("DOMContentLoaded", function(){


    createTimeOptions("startTime");

    createTimeOptions("endTime");


});




// ==========================
// 開始・終了時間作成
// ==========================

function createTimeOptions(id){


    const select =
        document.getElementById(id);



    if(!select){

        return;

    }



    for(let hour = 5; hour <= 20; hour++){


        for(let minute of [0,30]){


            let option =
                document.createElement("option");



            let time =
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
// モーダルを開く
// ==========================


function openModal(date){



    const modal =
        document.getElementById("modal");



    const selectedDate =
        document.getElementById("selectedDate");



    const dateInput =
        document.getElementById("date");



    const scheduleList =
        document.getElementById("scheduleList");





    // 日付表示

    selectedDate.textContent =
        date;



    // hiddenへ設定

    dateInput.value =
        date;



    // モーダル表示

    modal.style.display =
        "block";





    // 初期表示

    scheduleList.innerHTML =
        "読み込み中...";






    // ==========================
    // 保存済み予定取得
    // ==========================


    fetch("/schedule?date=" + date)



    .then(response => {


        if(!response.ok){


            throw new Error(
                "予定取得失敗"
            );


        }



        return response.json();


    })



    .then(data => {



        scheduleList.innerHTML =
            "";




        if(data.length === 0){


            scheduleList.innerHTML =
                "予定はありません";


            return;


        }






        data.forEach(schedule => {



            const div =
                document.createElement("div");



            div.className =
                "modal-schedule";




            div.innerHTML = `


                <hr>


                <div>
                ⏰ 
                ${schedule.startTime ?? ""}
                〜
                ${schedule.endTime ?? ""}
                </div>



                <div>
                🚜 
                ${schedule.workType ?? ""}
                </div>



                <div>
                🌱 
                ${schedule.cropName ?? ""}
                </div>



                <div>
                📍 
                ${schedule.fieldName ?? ""}
                </div>



                <div>
                👤 
                ${schedule.userName ?? ""}
                </div>



                <div>
                📝 
                ${schedule.schedule ?? ""}
                </div>



            `;




            scheduleList.appendChild(div);



        });




    })



    .catch(error => {



        console.error(error);



        scheduleList.innerHTML =
            "予定取得に失敗しました";



    });



}







// ==========================
// モーダルを閉じる
// ==========================


function closeModal(){


    const modal =
        document.getElementById("modal");



    modal.style.display =
        "none";


}







// ==========================
// 背景クリックで閉じる
// ==========================


window.onclick = function(event){



    const modal =
        document.getElementById("modal");



    if(event.target === modal){


        modal.style.display =
            "none";


    }


};






// ==========================
// 削除処理
// ==========================


function deleteSchedule(){


    const id =
        document.getElementById("scheduleId").value;



    if(!id){


        alert("削除する予定を選択してください");


        return;


    }




    fetch("/delete/" + id, {


        method:"POST"


    })



    .then(()=>{


        location.reload();


    });




}