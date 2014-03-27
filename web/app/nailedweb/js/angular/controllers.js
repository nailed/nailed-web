angular.module('nailed.controllers', [])
    .controller('BaseController', ["$scope", "$location", "$http", "UserService", function($scope, $location, $http, $user) {
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
        $scope.nailedVersion = Nailed.version;
        $scope.user = $user;
        $http.get("/api/mappacks.json").then(function(res){
            $scope.mappacks = res.data.mappacks;
        });
        $scope.login = function(auth){
            $user.login(auth.email, auth.password, function(success, message){
                if(!success){
                    alert(message);
                }
                $location.path("/");
            });
        }
        $scope.logout = function(){
            $scope.auth.email = "";
            $scope.auth.password = "";
            $user.logout();
        }
    }])
    .controller("MappackDetailController", function($scope, $routeParams, $http) {
        $scope.mappackName = $routeParams.mappackName
        $http.get("/api/mappacks/" + $routeParams.mappackName + ".json").then(function(res){
            $scope.mappack = res.data;
        });
    })
    .controller("RegisterController", function($scope, $http, $location) {

    });
