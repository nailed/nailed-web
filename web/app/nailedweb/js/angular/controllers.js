angular.module('nailed.controllers', [])
    .controller('BaseController', ["$scope", "$location", "$http", "UserService", "$route", function($scope, $location, $http, $user, $route) {
        $scope.auth = [];
        $scope.socket = {};
        $scope.isViewLoading = false;
        $scope.auth.signInText = "Sign in";
        $scope.auth.signInClass = "";
        $scope.auth.usernameClass = "";
        $scope.auth.passwordClass = "";
        $scope.hideLoginHeader = $location.path() === "/login";
        $scope.$on('$routeChangeStart', function(event, currRoute, prevRoute) {
            $scope.hideLoginHeader = $location.path() === "/login";
            if(currRoute.access && !currRoute.access.isFree){
                if(!$user.loggedIn){
                    $location.path('/login');
                    return;
                }else if(currRoute.access.permission){
                    var perm = currRoute.access.permission
                    if(!$user.permissions[perm]){
                        alert("You don't have the permissions to see this page")
                        $location.path('/');
                        //TODO: better error message!
                    }
                }
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
        $scope.login = function(auth){
            $scope.auth.signInText = "Signing in...";
            $scope.auth.signInClass = "disabled";
            $scope.auth.usernameClass = "";
            $scope.auth.passwordClass = "";
            $user.login(auth.username, auth.password, function(success, message){
                $scope.auth.signInText = "Sign in";
                $scope.auth.signInClass = "";
                if(!success){
                    if(message == "Unknown username"){
                        $scope.auth.username = "has-error";
                    }else if(message == "Invalid password"){
                        $scope.auth.passwordClass = "has-error";
                    }
                }else{
                    $scope.socket = io.connect(null,{'force new connection':true});
                }
                $location.path("/");
            });
        }
        $scope.logout = function(){
            $scope.auth.username = "";
            $scope.auth.password = "";
            $user.logout(function(){
                //TODO: we need to refresh access here. Forcing this is ugly, maybe.
                $location.path("/");
            });
            $scope.socket.disconnect();
        }
    }])
    .controller("MappackListController", function($scope, $routeParams, $http) {
        $http.get("/api/mappacks.json").then(function(res){
            $scope.mappacks = res.data.mappacks;
        });
        $scope.loadMappack = function(mappack){
            $http
                .post('/api/loadMappack/', "id=" + encodeURIComponent(mappack.mpid))
                .success(function(data, status, headers, config) {

                });
        }
    })
    .controller("MappackDetailController", function($scope, $routeParams, $http) {
        $scope.mappackName = $routeParams.mappackName
        $http.get("/api/mappacks/" + $routeParams.mappackName + ".json").then(function(res){
            $scope.mappack = res.data.mappack;
        });
    })
    .controller("RegisterController", ["$scope", "$location", "$http", "UserService", function($scope, $location, $http, $user) {
        $scope.register = [];
        $scope.register.usernameClass = "glyphicon-remove";
        $scope.register.emailClass = "glyphicon-remove";
        $scope.register.verifyClass = "glyphicon-remove";
        $scope.register.passwordClass = "glyphicon-remove";
        $scope.register.nameClass = "glyphicon-remove";
        $scope.register.buttonContent = "Sign Up!";
        $scope.register.buttonClass = "";
        $scope.register.password = "";
        $scope.register.passwordVerify = "";
        $scope.register.emailError = "";
        $scope.register.usernameError = "";
        $scope.register.name = "";
        $scope.register.error = [];
        $scope.register.error.visible = false;
        $scope.register.error.type = "";
        $scope.register.error.message = "";
        $scope.register.register = function(){
            $scope.register.buttonClass = "disabled";
            $scope.register.buttonContent = "Signing Up...";
            $scope.register.emailError = "";
            $scope.register.usernameError = "";
            $user.register($scope.register, function(success, message){
                $scope.register.error.visible = false;
                $scope.register.buttonClass = "";
                $scope.register.buttonContent = "Sign Up!";
                if(message == "OK"){
                    $location.path("/");
                }else if(message.indexOf("email") != -1){
                    $scope.register.error.visible = true;
                    $scope.register.error.type = "alert-danger";
                    $scope.register.error.message = message;
                    $scope.register.emailError = "has-error";
                }else if(message.indexOf("username") != -1){
                    $scope.register.error.visible = true;
                    $scope.register.error.type = "alert-danger";
                    $scope.register.error.message = message;
                    $scope.register.usernameError = "has-error";
                }
            });
        }
        var updateButton = function(){
            if($scope.register.usernameClass == "glyphicon-ok" && $scope.register.emailClass == "glyphicon-ok" && $scope.register.verifyClass == "glyphicon-ok" && $scope.register.passwordClass == "glyphicon-ok" && $scope.register.nameClass == "glyphicon-ok"){
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
        $scope.$watch("register.username", function(){
            if($scope.register.username.length > 1){
                $scope.register.usernameClass = "glyphicon-ok";
            }else{
                $scope.register.usernameClass = "glyphicon-remove";
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
        $scope.login.usernameClass = "glyphicon-remove";
        $scope.login.passwordClass = "glyphicon-remove";
        $scope.login.buttonContent = "Log in";
        $scope.login.buttonClass = "";
        $scope.login.password = "";
        $scope.login.username = "";
        $scope.login.error = [];
        $scope.login.error.visible = false;
        $scope.login.error.type = "";
        $scope.login.error.message = "";
        $scope.login.login = function(){
            $scope.login.buttonClass = "disabled";
            $scope.login.buttonContent = "Logging in...";
            $user.login($scope.login.username, $scope.login.password, function(success, message){
                $scope.login.buttonContent = "Log in";
                $scope.login.buttonClass = "";;
                if(!success){
                    $scope.login.error.visible = true;
                    $scope.login.error.type = "alert-danger";
                    $scope.login.error.message = message;
                }else{
                    $scope.login.password = "";
                    $scope.login.username = "";
                    $location.path("/");
                }
            });
        }
        var updateButton = function(){
            if($scope.login.usernameClass == "glyphicon-ok" && $scope.login.passwordClass == "glyphicon-ok"){
                $scope.login.buttonClass = "";
            }else{
                $scope.login.buttonClass = "disabled";
            }
        }
        $scope.$watch("login.username", function(){
            var regex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            if(regex.test($scope.login.username)){
                $scope.login.usernameClass = "glyphicon-ok";
            }else{
                $scope.login.usernameClass = "glyphicon-remove";
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
    }])
    .controller("MappackCreateController", ["$scope", "$location", "$http", "UserService", function($scope, $location, $http, $user) {
        $scope.cmp = []
        $scope.cmp.worldType = "void";
        $scope.cmp.worldSource = "build";
        $scope.cmp.gamemode = "0";
        $scope.cmp.difficulty = "0";
        $scope.cmp.enablePvp = true;
        $scope.cmp.preventBlockBreak = false;
        $scope.cmp.gametype = "default";
        $scope.cmp.gamerule = {};
        $scope.cmp.gamerule.doFireTick = true;
        $scope.cmp.gamerule.mobGriefing = true;
        $scope.cmp.gamerule.keepInventory = false;
        $scope.cmp.gamerule.doMobSpawning = true;
        $scope.cmp.gamerule.doMobLoot = true;
        $scope.cmp.gamerule.doTileDrops = true;
        $scope.cmp.gamerule.commandBlockOutput = false;
        $scope.cmp.gamerule.naturalRegeneration = true;
        $scope.cmp.gamerule.doDaylightCycle = true;
        $scope.cmp.spawns = {"villager":true,"squid":true,"mooshroom":true,"ocelot":true,"horse":true,"chicken":true,"pig":true,"sheep":true,"cow":true,"bat":true,"witherSkeleton":true,"silverfish":true,"magmaCube":true,"ghast":true,"blaze":true,"pigzombie":true,"wolf":true,"cavespider":true,"spider":true,"witch":true,"slime":true,"skeleton":true,"creeper":true,"zombie":true};
        $scope.cmp.create = function(){
            var formData = new FormData();
            formData.append("id", $scope.cmp.id);
            formData.append("name", $scope.cmp.name);
            formData.append("worldType", $scope.cmp.worldType);
            formData.append("worldSource", $scope.cmp.worldSource);
            formData.append("gamemode", $scope.cmp.gamemode);
            formData.append("enablePvp", $scope.cmp.enablePvp);
            formData.append("preventBlockBreak", $scope.cmp.preventBlockBreak);
            formData.append("difficulty", $scope.cmp.difficulty);
            formData.append("gametype", $scope.cmp.gametype);
            formData.append("gamerules", JSON.stringify($scope.cmp.gamerule));
            formData.append("spawns", JSON.stringify($scope.cmp.spawns));
            if($scope.files != undefined && $scope.files.length > 0) formData.append("mapFile", $scope.files[0]);
            //TODO: modify button state so the user knows it's uploading
            $http({
                method: 'POST',
                url: '/api/createMappack/',
                data: formData,
                headers: {'Content-Type': undefined}, transformRequest: angular.identity}
            ).success(function(data, status, headers, config) {
                //TODO: notify user that uploading is done
            });
        }
    }]);
