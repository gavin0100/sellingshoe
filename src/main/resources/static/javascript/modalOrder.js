$(document).ready(function () {
  $("table .delete").on("click", function () {
    const id = $(this).parent().find("#id").val();
    $("#deleteModal #deleteId").val(id);
    $.ajax({
      type: "GET",
      url: `/api/order/find/${id}`,
      success: function (order) {
        $("#deleteModal #idName2").text("Are you sure you want to remove order has id: \"" + order.id + "\" of customer named \"" + order.user.name +"\" from the list?");
      },
    });
  });

  $("table .edit").on("click", function () {
    const id = $(this).parent().find("#id").val();
    $.ajax({
      type: "GET",
      url: `/api/order/find/${id}`,
      success: function (order) {
        $("#editModal #saveOrderId").val(id);
        $("#editModal #user").val(order.user.id);
        $("#editModal #orderDate").val(order.orderDate);
        $("#editModal #email").val(order.email);
        $("#editModal #phoneNumber").val(order.phoneNumber);
        $("#editModal #address").val(order.address);
        $("#editModal #city").val(order.city);
        $("#editModal #zip").val(order.zip);
        $("#editModal #paymentMethod").val(order.paymentMethod);
        $("#editModal #total").val(order.total);
        $("#editModal #statusPayment").val(String(order.statusPayment));

        var statusPayment = order.statusPayment;
        console.log(statusPayment);
        // Xóa trạng thái 'disabled' của tất cả các options
        $("#editModal #statusPayment option").prop("disabled", false);

        // Đặt trạng thái 'disabled' dựa trên giá trị của statusPayment
        if (String(statusPayment) === "PENDING") {
          // Disable các options không được chọn
          $("#editModal #statusPayment option[value='FAILED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PENDING']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PAID_MOMO']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PAID_VNPAY']").prop("disabled", true);
          $("#editModal #statusPayment option[value='SHIPPING']").prop("disabled", true);
          $("#editModal #statusPayment option[value='DELIVERED']").prop("disabled", true);
        } else if (String(statusPayment) === "PAID_MOMO" || String(statusPayment) === "PAID_VNPAY") {
          // Disable các options không được chọn
          $("#editModal #statusPayment option[value='FAILED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PENDING']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PAID_MOMO']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PAID_VNPAY']").prop("disabled", true);
          $("#editModal #statusPayment option[value='CANCELED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='SHIPPING']").prop("disabled", true);
          $("#editModal #statusPayment option[value='DELIVERED']").prop("disabled", true);
        } else if (String(statusPayment) === "CONFIRMED"){
          $("#editModal #statusPayment option[value='FAILED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PENDING']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PAID_MOMO']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PAID_VNPAY']").prop("disabled", true);
          $("#editModal #statusPayment option[value='CANCELED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='DELIVERED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='CONFIRMED']").prop("disabled", true);
        } else if (String(statusPayment) === "SHIPPING"){
          $("#editModal #statusPayment option[value='FAILED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PENDING']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PAID_MOMO']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PAID_VNPAY']").prop("disabled", true);
          $("#editModal #statusPayment option[value='CANCELED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='SHIPPING']").prop("disabled", true);
          $("#editModal #statusPayment option[value='CONFIRMED']").prop("disabled", true);
        } else if (String(statusPayment) === "DELIVERED"){
          $("#editModal #statusPayment option[value='FAILED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PENDING']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PAID_MOMO']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PAID_VNPAY']").prop("disabled", true);
          $("#editModal #statusPayment option[value='DELIVERED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='SHIPPING']").prop("disabled", true);
          $("#editModal #statusPayment option[value='CONFIRMED']").prop("disabled", true);
        } else {
          $("#editModal #statusPayment option[value='FAILED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PENDING']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PAID_MOMO']").prop("disabled", true);
          $("#editModal #statusPayment option[value='PAID_VNPAY']").prop("disabled", true);
          $("#editModal #statusPayment option[value='CANCELED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='SHIPPING']").prop("disabled", true);
          $("#editModal #statusPayment option[value='CONFIRMED']").prop("disabled", true);
          $("#editModal #statusPayment option[value='DELIVERED']").prop("disabled", true);
        }
      },
    });
  });
});
