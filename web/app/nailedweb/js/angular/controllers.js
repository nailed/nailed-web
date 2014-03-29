angular.module('nailed.controllers', [])
    .controller('BaseController', ["$scope", "$location", "$http", "UserService", function($scope, $location, $http, $user) {
        $scope.auth = [];
        $scope.isViewLoading = false;
        $scope.auth.signInText = "Sign in";
        $scope.auth.signInClass = "";
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
            $scope.auth.signInText = "Signing in...";
            $scope.auth.signInClass = "disabled";
            $user.login(auth.email, auth.password, function(success, message){
                $scope.auth.signInText = "Sign in";
                $scope.auth.signInClass = "";
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
    .controller("RegisterController", ["$scope", "$location", "$http", "UserService", function($scope, $location, $http, $user) {
        $scope.register = [];
        $scope.register.emailClass = "glyphicon-remove";
        $scope.register.verifyClass = "glyphicon-remove";
        $scope.register.passwordClass = "glyphicon-remove";
        $scope.register.nameClass = "glyphicon-remove";
        $scope.register.buttonContent = "Sign Up!";
        $scope.register.buttonClass = "";
        $scope.register.password = "";
        $scope.register.passwordVerify = "";
        $scope.register.email = "";
        $scope.register.emailError = "";
        $scope.register.name = "";
        $scope.register.error = [];
        $scope.register.error.visible = false;
        $scope.register.error.type = "";
        $scope.register.error.message = "";
        $scope.register.register = function(){
            $scope.register.buttonClass = "disabled";
            $scope.register.buttonContent = "Signing Up...";
            $scope.register.emailError = "";
            $user.register($scope.register, function(success, message){
                $scope.register.error.visible = false;
                $scope.register.buttonClass = "";
                $scope.register.buttonContent = "Sign Up!";
                if(message == "OK"){
                    $location.path("/");
                }else{
                    $scope.register.error.visible = true;
                    $scope.register.error.type = "alert-danger";
                    $scope.register.error.message = message;
                    $scope.register.emailError = "has-error";
                }
            });
        }
        var updateButton = function(){
            if($scope.register.emailClass == "glyphicon-ok" && $scope.register.verifyClass == "glyphicon-ok" && $scope.register.passwordClass == "glyphicon-ok" && $scope.register.nameClass == "glyphicon-ok"){
                $scope.register.buttonClass = "";
            }else{
                $scope.register.buttonClass = "disabled";
            }
        }
        var updatePasswords = function(){
            var passValid = false;
            if($scope.register.password.length >= 6){
                $scope.register.passwordClass = "glyphicon-ok";
                passValid = true;
            }else{
                $scope.register.passwordClass = "glyphicon-remove";
            }
            if($scope.register.password === $scope.register.passwordVerify && passValid){
                $scope.register.verifyClass = "glyphicon-ok";
            }else{
                $scope.register.verifyClass = "glyphicon-remove";
            }
            updateButton();
        };
        $scope.$watch("register.password", updatePasswords, true);
        $scope.$watch("register.passwordVerify", updatePasswords, true);
        $scope.$watch("register.email", function(){
            var regex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            if(regex.test($scope.register.email)){
                $scope.register.emailClass = "glyphicon-ok";
            }else{
                $scope.register.emailClass = "glyphicon-remove";
            }
            updateButton();
        }, true);
        $scope.$watch("register.name", function(){
            if($scope.register.name.length > 1){
                $scope.register.nameClass = "glyphicon-ok";
            }else{
                $scope.register.nameClass = "glyphicon-remove";
            }
            updateButton();
        }, true);
    }]);
