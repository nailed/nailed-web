angular.module('nailed.controllers', [])
    .controller('BaseController', ["$scope", "$location", "$http", "UserService", "$route", function($scope, $location, $http, $user, $route) {
        $scope.auth = [];
        $scope.isViewLoading = false;
        $scope.auth.signInText = "Sign in";
        $scope.auth.signInClass = "";
        $scope.auth.emailClass = "";
        $scope.auth.passwordClass = "";
        $scope.hideLoginHeader = $location.path() === "/login";
        $scope.$on('$routeChangeStart', function(event, currRoute, prevRoute) {
            $scope.hideLoginHeader = $location.path() === "/login";
            if((currRoute.access && !currRoute.access.isFree) && !$user.loggedIn){
                $location.path('/login');
                return;
            }
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
            $scope.auth.emailClass = "";
            $scope.auth.passwordClass = "";
            $user.login(auth.email, auth.password, function(success, message){
                $scope.auth.signInText = "Sign in";
                $scope.auth.signInClass = "";
                if(!success){
                    if(message == "Unknown email address"){
                        $scope.auth.emailClass = "has-error";
                    }else if(message == "Invalid password"){
                        $scope.auth.passwordClass = "has-error";
                    }
                }
                $location.path("/");
            });
        }
        $scope.logout = function(){
            $scope.auth.email = "";
            $scope.auth.password = "";
            $user.logout(function(){
                //TODO: we need to refresh access here. Forcing this is ugly, maybe.
                $location.path("/");
            });
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
    }])
    .controller("LinkMojangController", ["$scope", "$location", "$http", "UserService", function($scope, $location, $http, $user) {
        $scope.linkMojang = [];
        $scope.linkMojang.emailClass = "glyphicon-remove";
        $scope.linkMojang.passwordClass = "glyphicon-remove";
        $scope.linkMojang.buttonContent = "Link account";
        $scope.linkMojang.buttonClass = "";
        $scope.linkMojang.password = "";
        $scope.linkMojang.email = "";
        $scope.linkMojang.error = [];
        $scope.linkMojang.error.visible = false;
        $scope.linkMojang.error.type = "";
        $scope.linkMojang.error.message = "";
        $scope.linkMojang.link = function(){
            $scope.linkMojang.buttonClass = "disabled";
            $scope.linkMojang.buttonContent = "Linking...";
            $http
            .post('/api/link/', "email=" + encodeURIComponent($scope.linkMojang.email) + "&password=" + encodeURIComponent($scope.linkMojang.password))
            .success(function(data, status, headers, config) {
                $scope.linkMojang.error.visible = false;
                $scope.linkMojang.buttonClass = "";
                $scope.linkMojang.buttonContent = "Link account";
                if(data.status == "ok"){
                    $scope.linkMojang.error.visible = true;
                    $scope.linkMojang.error.type = "alert-success";
                    $scope.linkMojang.error.message = "Successfully linked your minecraft account to your nailed account";
                }else if(data.status == "error"){
                    $scope.linkMojang.error.visible = true;
                    $scope.linkMojang.error.type = "alert-danger";
                    $scope.linkMojang.error.message = data.error;
                }
            });
        }
        var updateButton = function(){
            if($scope.linkMojang.emailClass == "glyphicon-ok" && $scope.linkMojang.passwordClass == "glyphicon-ok"){
                $scope.linkMojang.buttonClass = "";
            }else{
                $scope.linkMojang.buttonClass = "disabled";
            }
        }
        $scope.$watch("linkMojang.email", function(){
            if($scope.linkMojang.email.length > 0){
                $scope.linkMojang.emailClass = "glyphicon-ok";
            }else{
                $scope.linkMojang.emailClass = "glyphicon-remove";
            }
            updateButton();
        }, true);
        $scope.$watch("linkMojang.password", function(){
            if($scope.linkMojang.password.length > 0){
                $scope.linkMojang.passwordClass = "glyphicon-ok";
            }else{
                $scope.linkMojang.passwordClass = "glyphicon-remove";
            }
            updateButton();
        }, true);
    }])
    .controller("LoginController", ["$scope", "$location", "$http", "UserService", function($scope, $location, $http, $user) {
        $scope.login = [];
        $scope.login.emailClass = "glyphicon-remove";
        $scope.login.passwordClass = "glyphicon-remove";
        $scope.login.buttonContent = "Log in";
        $scope.login.buttonClass = "";
        $scope.login.password = "";
        $scope.login.email = "";
        $scope.login.error = [];
        $scope.login.error.visible = false;
        $scope.login.error.type = "";
        $scope.login.error.message = "";
        $scope.login.login = function(){
            $scope.login.buttonClass = "disabled";
            $scope.login.buttonContent = "Logging in...";
            $user.login($scope.login.email, $scope.login.password, function(success, message){
                $scope.login.buttonContent = "Log in";
                $scope.login.buttonClass = "";;
                if(!success){
                    $scope.login.error.visible = true;
                    $scope.login.error.type = "alert-danger";
                    $scope.login.error.message = message;
                }else{
                    $scope.login.password = "";
                    $scope.login.email = "";
                    $location.path("/");
                }
            });
        }
        var updateButton = function(){
            if($scope.login.emailClass == "glyphicon-ok" && $scope.login.passwordClass == "glyphicon-ok"){
                $scope.login.buttonClass = "";
            }else{
                $scope.login.buttonClass = "disabled";
            }
        }
        $scope.$watch("login.email", function(){
            var regex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            if(regex.test($scope.login.email)){
                $scope.login.emailClass = "glyphicon-ok";
            }else{
                $scope.login.emailClass = "glyphicon-remove";
            }
            updateButton();
        }, true);
        $scope.$watch("login.password", function(){
            if($scope.login.password.length > 6){
                $scope.login.passwordClass = "glyphicon-ok";
            }else{
                $scope.login.passwordClass = "glyphicon-remove";
            }
            updateButton();
        }, true);
    }]);
