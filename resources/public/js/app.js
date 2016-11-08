angular.module('cashApp', ['ui.bootstrap']).controller('cashController', function ($scope, $http) {
  $http.get('api/v0.1/category/').success(function(category_list) {
    $scope.categories = category_list;
  });

  $http.get('api/v0.1/balance/').success(function(balance) {
    $scope.balance = balance.money;
  });

  $http.get('api/v0.1/account/').success(function(account_list) {
    $scope.accounts = account_list;
  });

  $http.get('api/v0.1/data/').success(function(data_list) {
    $scope.datas = [];
    for (key in data_list) {
      var data = data_list[key];
      for (key in $scope.categories) {
        if ($scope.categories[key].id === data["category"]) {
          data["category"] = $scope.categories[key].name;
          break;
        }
      }
      for (key in $scope.accounts) {
        if ($scope.accounts[key].id === data["account"]) {
          data["account"] = $scope.accounts[key].name;
          break;
        }
      }
      data.input_time = moment.unix(data.input_time).format("YYYY. M. D hh:mm:ss");
      $scope.datas.push(data);
    }
  });

  $scope.isCollapsed = true;

  $scope.updateData = function(data) {
    for (key in $scope.categories) {
      if ($scope.categories[key].id === data["category"]) {
        data["category"] = $scope.categories[key].name;
        break;
      }
    }
    for (key in $scope.accounts) {
        if ($scope.accounts[key].id === data["account"]) {
          data["account"] = $scope.accounts[key].name;
          break;
        }
      }
    data.input_time = moment.unix(data.input_time).format("YYYY. M. D hh:mm:ss");
    $scope.datas.push(data);
  };

  $scope.addData = function() {
    if (($scope.item && $scope.money) && isNaN($scope.money) == false) {
      var data = {"input_time":moment().unix(),
                  "item" : $scope.item,
                  "money" : Number($scope.money),
                  "category" : Number($scope.selectedCategory.id),
                  "account" : Number($scope.selectedAccount.id)};
      $http.post('api/v0.1/data/', JSON.stringify(data));
      $scope.updateData(data);
      $scope.item = null;
      $scope.money = null;
    }	else {
      alert('올바른 값을 입력해주세요!');
    }
  };

  $scope.updateCategory = function(category) {
      $scope.categories.push(category);
      $scope.newCategory = null;
  }

  $scope.addCategory = function() {
    $http.post('api/v0.1/category/',{"name" : $scope.newCategory,
                                     "money" : 0,
                                     "type" : $scope.newCategoryType})
    .success(function(result) {
      var category = {"id" : result.id,
                      "name" : $scope.newCategory,
                      "money" : 0,
                      "type" : $scope.newCategoryType};
      $scope.updateCategory(category);
    });
  };

  $scope.deleteCategory = function() {
    $http.delete('api/v0.1/category/' + $scope.toDeleteCategory.id + '/');
    var index;
    for (index in $scope.categories) {
      if ($scope.categories[index].id === Number($scope.toDeleteCategory.id)) {
        break;
      }
    }
    delete $scope.categories[index];
    $scope.toDeleteCategory = null;
    console.log($scope.options);
  };

  $scope.updateAccount = function(account) {
    $scope.accounts.push(account);
    $scope.newAccount = null;
  }

  $scope.addAccount = function() {
    $http.post('api/v0.1/account/',{"name" : $scope.newAccount,
                                     "balance" : 0})
    .success(function(result) {
      var account = {"id" : result.id,
                      "name" : $scope.newAccount,
                      "balance" : 0};
      $scope.updateAccount(account);
    });
  };

  $scope.deleteAccount = function() {
    $http.delete('api/v0.1/account/' + $scope.toDeleteAccount.id + '/');
    var index;
    for (index in $scope.accounts) {
      if ($scope.accounts[index].id === Number($scope.toDeleteAccount.id)) {
        break;
      }
    }
    delete $scope.accounts[index];
    $scope.toDeleteAccount = null;
    console.log($scope.options);
  };

  var _selected;
  $scope.selected = undefined;

  $scope.ngModelOptionsSelected = function(value) {
    if (arguments.length) {
      _selected = value;
    } else {
      return _selected;
    }
  };
});
