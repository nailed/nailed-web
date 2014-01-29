angular.module('nailed.controllers', [])
    .controller('BaseController', function($scope, $location, $http) {
        $scope.isViewLoading = false;
        $scope.$on('$routeChangeStart', function() {
            $scope.isViewLoading = true;
        });
        $scope.$on('$routeChangeSuccess', function() {
            $scope.isViewLoading = false;
        });
        $scope.isActive = function(route) {
            return route === $location.path();
        }
        $scope.userInfo = Nailed.userInfo;
        $scope.nailedVersion = Nailed.version;
        $http.get("/api/mappacks.json").then(function(res){
            $scope.mappacks = res.data.mappacks;
        });
    })
    .controller("MappackDetailController", function($scope, $routeParams, $http) {
        $scope.mappackName = $routeParams.mappackName
        $http.get("/api/mappacks/" + $routeParams.mappackName + ".json").then(function(res){
            $scope.mappack = res.data;
        });
    });
