(function(window) {

    function NailedCore(win) {
        this.logger = new win.NailedLogger();
        this.networkManager = new win.NetworkManager(this);

        win.NetworkManager = undefined;
        win.NailedLogger = undefined;

        //Initialize soundcloud api
        SC.initialize({client_id: "cf33b0ff49ce042b704c49de99a08818"});
    }

    NailedCore.prototype.getScope = function(){
        return angular.element("body").scope();
    }

    NailedCore.prototype.version = "0.1-SNAPSHOT";
    NailedCore.prototype.BOOTED = false;

    NailedCore.prototype.handleFatal = function(error){
        //TODO: implement
        this.logger.error("Fatal error: " + error);
    }

    window.Nailed = new NailedCore(window);

})(window);
