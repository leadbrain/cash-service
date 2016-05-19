function todoController($scope, $http) {
  $http.get('api/v0.1/data/').success(function(data_list) {
    $scope.datas = data_list;
  });

  $scope.addTodo = function() {
    if (($scope.item && $scope.money) && isNaN($scope.money) == false) {
      var data = {"item" : $scope.item,
                  "money" : $scope.money};
      $scope.datas.push(data);
      $http.post('api/v0.1/data/', data);
      $scope.item = null;
      $scope.money = null;
    }	else {
      alert('올바른 값을 입력해주세요!');
    }
  };
}
