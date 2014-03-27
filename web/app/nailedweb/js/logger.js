(function(window){

    window.NailedLogger = function(){}

    var logformat = "dd-mm-yyyy HH:MM:ss";

    window.NailedLogger.prototype.console = window.console || undefined
    window.NailedLogger.prototype.debug = function(data){
        if(typeof this.console !== undefined){
            this.console.debug(new Date().format(logformat) + " [DEBUG] " + data);
        }
    }
    window.NailedLogger.prototype.info = function(data){
        if(typeof this.console !== undefined){
            this.console.log(new Date().format(logformat) + " [INFO] " + data);
        }
    }
    window.NailedLogger.prototype.warn = function(data){
        if(typeof this.console !== undefined){
            this.console.warn(new Date().format(logformat) + " [WARNING] " + data);
        }
    }
    window.NailedLogger.prototype.error = function(data){
        if(typeof this.console !== undefined){
            this.console.error(new Date().format(logformat) + " [SEVERE] " + data);
        }
    }
    window.NailedLogger.prototype.onMissingDependency = function(name){
        alert("Dependency " + name + " is missing");
    }

})(window);