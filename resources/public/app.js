function todoController($scope, $http) {
  $http.get('api/v0.1/category/').success(function(category_list) {
    $scope.categories = category_list;
  });

  $http.get('api/v0.1/data/').success(function(data_list) {
    $scope.datas = [];
    for (key in data_list) {
      var data = data_list[key];
      data.input_time = moment.unix(data.input_time).format("YYYY. M. D hh:mm:ss");
      $scope.datas.push(data);
    }
  });

  $scope.updateData = function(data) {
    for (key in $scope.categories) {
      if ($scope.categories[key].id === data["category"]) {
        data["category"] = $scope.categories[key].name;
        break;
      }
    }
    data.input_time = moment.unix(data.input_time).format("YYYY. M. D hh:mm:ss");
    $scope.datas.push(data);
  };

  $scope.addTodo = function() {
    if (($scope.item && $scope.money) && isNaN($scope.money) == false) {
      var data = {"input_time":moment().unix(),
                  "item" : $scope.item,
                  "money" : $scope.money,
                  "category" : Number($scope.selectedCategory)};
      $http.post('api/v0.1/data/', data);
      $scope.updateData(data);
      $scope.item = null;
      $scope.money = null;
    }	else {
      alert('올바른 값을 입력해주세요!');
    }
  };
}
