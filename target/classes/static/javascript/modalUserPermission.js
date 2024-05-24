$(document).ready(function () {
  $("table .edit").on("click", function () {
    const id = $(this).parent().find("#id").val();
    $.ajax({
      type: "GET",
      url: `/api/user-permission/find/${id}`,
      success: function (userPermission) {
        console.log(userPermission.categoryManagement);
        $("#editModal #saveId").val(id);
        $("#editModal #role").val(userPermission.role);
        $("#editModal #editCategory").val(userPermission.categoryManagement);
        $("#editModal #editOrder").val(userPermission.orderManagement);
        $("#editModal #editProduct").val(userPermission.productManagement);
        $("#editModal #editUser").val(userPermission.userManagement);
        $("#editModal #editStaff").val(userPermission.staffManagement);
        $("#editModal #editMaterial").val(userPermission.materialManagement);
        $("#editModal #editPlaceOrder").val(userPermission.placeOrderManagement);
      },
    });
  });
});
