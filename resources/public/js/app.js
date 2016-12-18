angular.module('cashApp', ['ui.bootstrap']).controller('cashController', function ($scope, $http) {
  $scope.input = {
    item : null,
    money : null,
    incomeCategory : null,
    incomeAccount : null,
    spendCategory : null,
    spendAccount : null,
    fromAccount : null,
    toAccount : null
  };

  $http.get('api/v0.1/category/').success(function(category_list) {
    $scope.inCategories = [];
    $scope.outCategories = [];
    $scope.categoryMap = [];
    for (key in category_list) {
      var category = category_list[key];
      if (category.type == 'in') {
        $scope.inCategories.push(category)
      }
      if (category.type == 'out') {
        $scope.outCategories.push(category)
      }
      $scope.categoryMap[category.id] = category.name;
    }
  });

  $http.get('api/v0.1/balance/').success(function(balance) {
    $scope.asset = balance.asset;
    $scope.debt = balance.debt;
  });

  $http.get('api/v0.1/account/').success(function(account_list) {
    $scope.accounts = account_list;
    $scope.accountMap = [];
    for (key in account_list) {
      $scope.accountMap[account_list[key].id] = account_list[key].name;
    }
  });

  $http.get('api/v0.1/data/').success(function(data_list) {
    $scope.datas = [];
    for (key in data_list) {
      var data = data_list[key];
      if (data.from_type == "category") {
        data.from_name = $scope.categoryMap[data.from_id];
      }

      if (data.to_type == "category") {
        data.to_name = $scope.categoryMap[data.to_id];
      }

      if (data.from_type == "account") {
        data.from_name = $scope.accountMap[data.from_id];
      }

      if (data.to_type == "account") {
        data.to_name = $scope.accountMap[data.to_id];
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

  $scope.addIncomeData = function() {
    console.log($scope.input.incomeCategory);
    console.log($scope.input.incomeAccount);
    console.log($scope.input.item);
    console.log($scope.input.money);
    var data = {"input_time":moment().unix(),
                "item" : $scope.input.item,
                "amount" : Number($scope.input.money),
                "from_type" : "category",
                "from_id": Number($scope.input.incomeCategory.id),
                "to_type" : "account",
                "to_id" : Number($scope.input.incomeAccount.id)};
    $scope.addData(data);
    $scope.input.incomeAccount = null;
    $scope.input.incomeCategory = null;
  };

  $scope.addSpendData = function() {
    var data = {"input_time":moment().unix(),
                "item" : $scope.input.item,
                "amount" : Number($scope.input.money),
                "to_type" : "category",
                "to_id": Number($scope.input.spendCategory.id),
                "from_type" : "account",
                "from_id" : Number($scope.input.spendAccount.id)};
    $scope.addData(data);
    $scope.input.spendAccount = null;
    $scope.input.spendCategory = null;
  };

  $scope.addTransferData = function() {
    var data = {"input_time":moment().unix(),
                "item" : $scope.input.item,
                "amount" : Number($scope.input.money),
                "from_type" : "account",
                "from_id": Number($scope.input.fromAccount.id),
                "to_type" : "account",
                "to_id" : Number($scope.input.toAccount.id)};
    $scope.addData(data);
    $scope.input.fromAccount = null;
    $scope.input.toAccount = null;
  };

  $scope.addData = function(data) {
    if (($scope.input.item && $scope.input.money) && isNaN($scope.input.money) == false) {
      $http.post('api/v0.1/data/', JSON.stringify(data));
      $scope.updateData(data);
      $scope.input.item = null;
      $scope.input.money = null;
    }	else {
      alert('올바른 값을 입력해주세요!');
    }
  };

  $scope.updateCategory = function(category) {
    if (category.type == "in") {
      $scope.inCategories.push(category);
    }
    if (category.type == "out") {
      $scope.outCategories.push(category);
    }
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

    var targetCategories = null;
    if ($scope.toDeleteCategory.type == "in") {
      targetCategories = $scope.inCategories;
    }
    if ($scope.toDeleteCategory.type == "out") {
      targetCategories = $scope.outCategories;
    }
    var index;
    for (index in targetCategories) {
      if (targetCategories[index].id === Number($scope.toDeleteCategory.id)) {
        break;
      }
    }
    delete targetCategories[index];
    $scope.toDeleteCategory = null;
    console.log($scope.options);
  };

  $scope.updateAccount = function(account) {
    $scope.accounts.push(account);
    $scope.newAccount = null;
    $scope.newAccountType = null;
  }

  $scope.addAccount = function() {
    $http.post('api/v0.1/account/',{"name" : $scope.newAccount,
                                    "balance" : 0,
                                    "type" : $scope.newAccountType})
    .success(function(result) {
      var account = {"id" : result.id,
                     "name" : $scope.newAccount,
                     "balance" : 0,
                     "type" : $scope.newAccountType};
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
