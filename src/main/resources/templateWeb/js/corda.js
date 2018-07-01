"use strict";
const app = angular.module('demoAppModule', ['ui.bootstrap']);
app.config(['$qProvider', function ($qProvider) {
    $qProvider.errorOnUnhandledRejections(false);
}]);

app.controller('AgreementController', function($http, $scope, $location, $uibModal, $rootScope) {
    const demoApp = this;

    // We identify the node.
    $rootScope.apiBaseURL = "/api/template/";
    $rootScope.peers = [];
	$rootScope.allParties = [];
	let agreements = [];

	$http.get($rootScope.apiBaseURL + "getAgreements").then((response) => agreements = response.data.me);
	$http.get($rootScope.apiBaseURL + "peers").then((response) => $rootScope.peers = response.data.peers);

	$http.get($rootScope.apiBaseURL + "allparties")
        .then(function(response) {
			$rootScope.allParties = response.data.allParties;
		});

	$http.get($rootScope.apiBaseURL + "me")
        .then(function(response) {
			$rootScope.thisNode = response.data.me;
		});

	$rootScope.collateral = [{id: 1,name:"CASH"},{id: 2,name:"SECURITIES"}];
	$rootScope.threshold = [{id: 1,name:"AAA"},{id: 2,name:"AA+ to AA-"},{id: 3,name:"A+ to A-"},{id: 4,name:"BBB+ or below"}];
	$rootScope.yesNo = [{id: 1,name:"Yes"},{id: 0,name:"No"}];
	$rootScope.deliveryAmount = [{id: 1,name:"Party A only pays"},{id: 2,name:"Party B only pays"},{id: 3,name:"Both Party A and Party B pay"}];
	$rootScope.moodys = [{id: 1,name:"AAA"},{id: 2,name:"AA1 to AA3"},{id: 3,name:"A1 to A3"},{id: 4,name:"Baa1 or below"}];
	$rootScope.sps = [{id: 1,name:"AAA"},{id: 2,name:"AA+ to AA-"},{id: 3,name:"A+ to A-"},{id: 4,name:"BBB+ or below"}];
	$rootScope.fitchs = [{id: 1,name:"AAA"},{id: 2,name:"AA"},{id: 3,name:"A"},{id: 4,name:"BBB"},{id: 5,name:"BB"},{id: 6,name:"CCC"},{id: 7,name:"D"}];

	$rootScope.currencies = ["USD","GBP","EUR","AED","ALL","AMD","AON","ARS","ATS","AUD","AWG","AZM","AZN","BBD","BDT","BEF","BGN","BHD","BMD","BND","BOB","BRL","BSD","BWP","BYR","BZD","CAD","CHF","CLP","CNY","COP","CRC","CSD","CUP","CVE","CYP","CZK","DEM","DJF","DKK","DOP","DZD","ECS","EEK","EGP","ESP","FIM","FRF","GEL","GHC","GHS","GIP","GNF","GRD","GTQ","GYD","HKD","HNL","HRK","HTG","HUF","IDR","IEP","ILS","INR","ISK","ITL","JMD","JOD","JPY","KES","KPW","KRW","KWD","KYD","KZT","LAK","LBP","LKR","LRD","LSL","LTL","LUF","LVL","LYD","MAD","MDL","MMK","MNT","MOP","MUR","MXN","MYR","MZM","NAD","NGN","NIO","NLG","NOK","NPR","NZD","OMR","PAB","PEN","PGK","PHP","PKR","PLN","PTE","PYG","QAR","ROL","RON","RSD","RUB","SAR","SCR","SDD","SEK","SGD","SHP","SIT","SKK","SLL","SOS","STD","SVC","THB","TND","TOP","TRL","TRX","TRY","TTD","TWD","UAH","UGX","UYU","UZS","VEB","VEF","VND","VUV","XEU","XOF","XPF","YER","ZAR","ZMK","ZWD","ZWR"];

	$rootScope.products = ["ABS Swap","Actively Managed ETF","Agriculture Commodity","Agriculture Commodity Forward","Agriculture Commodity NDF","Agriculture Commodity Option","Agriculture Commodity Swap","Algorithmic Index Basket Total Return Option","Algorithmic Index Basket Total Return Swap","Algorithmic Index LIBOR Option","Algorithmic Index Total Return Option","Algorithmic Index Total Return Swap","American FX Option","Asian FX Option","AutoCallabe Bullish Collar","AutoCallabe Option","Average Rate Forward FX","Basis Swap","Bermudan Corridor","Bermudan Double CMS Corridor","Bermudan Double Equity Corridor","Bermudan FX Option","Bermudan Quanto Corridor","Bermudan Quanto Option","Bermudan Spread Corridor","Bermudan Swaption","Bond Buy/Sell Back","Bond Cash Flows TRS","Bond ETF","Bond Future Name","Bond Option","Bond Option Name","Bond Repo","Bond Reverse Repo","Bond Sell/Buy Back","Bond Spread Option","Bond Swap","Bullet Payment","Callable  Bond Repo","Callable Bond Asset Swap","Callable Reverse Floater","Cash Balance","Certificate of Deposit","Commercial Paper","Commodity (ex Gold) Barrier Option","Commodity (ex Gold) Digital Option","Commodity (ex Gold) Future Name","Commodity (ex Gold) Option Name","Commodity (ex Gold) Swaption","Commodity ETF","Commodity Index Future Name","Commodity Index Name","Commodity Index Option Name","Constant Maturity Swap","Constant Maturity Swap Cap","Constant Maturity Swap Cap/Floor Spread Option","Constant Maturity Swap Floor","Contingent CDS","Convertible Bond","Convertible Bond Asset Swap","Convertible Bond Future Name","Convertible Bond Option","Convertible Bond Option Name","Convertible Preference Share","Corporate","Credit Linked IR Option: Bermudan CDO Corridor","Credit Spread Option","Cross Currency Basis Swap","Cross Currency Fixed / Fixed Swap","Cross Currency Fixed / Float Swap","Cross Currency Float / Float Swap","Cross Currency NDF","Currency ETF","Custom Bond Repo","Deposit","Depository Receipt","Digital FX Option","Double No Touch FX Barrier Option","Double One Touch FX Barrier Option","Dual Currency Constant Maturity Swap","Energy Commodity","Energy Commodity Forward","Energy Commodity NDF","Energy Commodity Option","Energy Commodity Swap","Equity Accumulator","Equity Asian Option","Equity Barrier","Equity Barrier Option","Equity Basket Asian Option","Equity Basket CFD","Equity Basket Composite European Option","Equity Basket Dividend Option","Equity Basket Dividend Swap","Equity Basket European Option","Equity Basket Option","Equity Basket Option Super Asianing","Equity Basket Option TR Asianing","Equity Basket Portfolio Swap","Equity Basket Selection Option","Equity Basket Swap","Equity Basket v Individual Option","Equity Basket Variance Option","Equity Basket Variance Swap","Equity Basket Volatility Option","Equity Basket Volatility Swap","Equity Buy/Sell Back","Equity CFD","Equity Conditional Outperformance Option","Equity Correlation Swap","Equity Dividend Option","Equity Dividend Swap","Equity European Composite Option","Equity European Digital Option","Equity Future Name","Equity Futures Swap","Equity Index CFD","Equity Index Dividend Option","Equity Index Dividend Swap","Equity Index Future Name","Equity Index Name","Equity Index Option","Equity Index Option Name","Equity Index Portfolio Swap","Equity Index Swap","Equity Index Variance Option","Equity Index Variance Swap","Equity Index Volatility Option","Equity Index Volatility Swap","Equity Index Warrant","Equity Option","Equity Option Name","Equity Outperformance Option","Equity Plain Option with Control Portfolio","Equity Range Accrual","Equity Repo","Equity Reverse Repo","Equity Sell/Buy Back","Equity Spread Option","Equity Swap","Equity Swaption","Equity Variance Option","Equity Variance Swap","Equity Volatility Option","Equity Volatility Swap","Equity Warrant","ETF Future Name","ETF Option","ETF Option Name","European FX Option","Exchage Traded Grantor Trust","Exchangeable Bond","Exotic Agriculture Commodity Option","Exotic Energy Commodity Option","Exotic FX Option","Exotic Gold Commodity Option","Exotic Metals (ex Gold) Commodity Option","First to Default CDS","Fixed / Fixed IRS","Fixed / Float IRS","Float / Float IRS","Floating Rate Note","Floating Rate Note Option","Forward  Bond","Forward Equity","Forward Equity Basket","Forward Equity Index","Forward ETF","Forward FX","Forward FX Basket","Forward Interest Rate","Forward Start Swaption","FRA","FX Basket Option","FX Forward Rate Agreement","FX Forward Volatility Agreement","FX Future Name","FX GTARF","FX Option Name","FX Target Redemption Forward","FX TARN","FX Time Option","FX Variance Swap","FX Volatility Swap","Gold Commodity","Gold Commodity Barrier Option","Gold Commodity Digital Option","Gold Commodity Forward","Gold Commodity Future Name","Gold Commodity NDF","Gold Commodity Option","Gold Commodity Performance Swap","Gold Commodity Swap","Gold Commodity Swaption","Index CDO","Index CDS","Index Credit Swaption","Index Credit TRS","Index ETF","Index Tranche CDO","Index Tranche CDS","Index Tranche Credit Swaption","Index Tranche Credit TRS","Inflation Asset Swap","Inflation Cap","Inflation Cap Zero Coupon","Inflation Cross Currency Swap","Inflation Exotic Bond Asset Swap","Inflation Floor","Inflation Floor Zero Coupon","Inflation Linked  Bond","Inflation Quanto Cap","Inflation Quanto Floor","Inflation Swap","Inflation Zero Coupon Swap","Interest Rate Barrier","Interest Rate Bermudan Option","Interest Rate Callable Corridor","Interest Rate Cap","Interest Rate Corridor","Interest Rate Debt Derivative","Interest Rate Digital","Interest Rate Floor","Interest Rate Snowball","Inverse ETF","IR Future Contract Name","IR Option Contract Name","Japanes Government Bond Gen-Saki","Japanes Government Bond Reverse Gen-Saki","Leveraged ETF","Limited Price Inflation Swap","Loan","Loan/Lease Agriculture Commodity","Loan/Lease Energy Commodity","Loan/Lease Gold Commodity","Loan/Lease Metals (ex Gold) Commodity","Loan/Lease Other Commodity","Medium Term Note","Metals (ex Gold) Commodity","Metals (ex Gold) Commodity Forward","Metals (ex Gold) Commodity NDF","Metals (ex Gold) Commodity Option","Metals (ex Gold) Commodity Swap","Mortgage Backed Security","Mortgage Option","Mortgage TBA","Multi Commodity Exotic Option","Non-Deliverable FX Forward","Non-Deliverable FX Option","Non-UCITS Fund Units","nth to Default CDS","OIS","Other Commodity","Other Commodity Forward","Other Commodity NDF","Other Commodity Option","Other Commodity Performance Swap","Other Commodity Swap","Other Credit Derivative Option","Other Equity Basket Derivative","Other Equity Derivative","Other Equity Index Derivative","Other Exotic Commodity Option","Other Exotic Interest Rate Derivative","Other Interest Rate Derivative","Pay to Hold","Perpetual  Bond","Perpetual Floating Rate Note","Power Reverse Dual Currency","Preference Share","Quanto Option","Rainbow Best Of Option","Rainbow Worst Of Option","Range Accrual Swap","Regular Bond","Regular Equity","Single Equity Portfolio Swap","Single Name Corporate CDO","Single Name Corporate CDS","Single Name Corporate Credit Swaption","Single Name Corporate Credit TRS","Single Name Muni Credit Swaption","Single Name Sovereign CDO","Single Name Sovereign CDS","Single Name Sovereign Credit Swaption","Single Name Sovereign Credit TRS","Single No Touch FX Barrier Option","Single One Touch FX Barrier Option","Spot FX","Spread Equity Dividend Option","Spread Equity Variance Option","Spread Equity Volatility Option","Stock Borrow","Stock ETF","Stock Loan","Structured  Loan","Structured CDS","Swap FX","Swaption","Synthetic Bond Future Option","Synthetic CDO","Synthetic Convertible Bond","Synthetic Equity","Targeted Accrual Redemption Note (TARN)","To Be Announced (TBA)","Total Return Swap","Treasury Bills","UCITS Fund Units","Warrant","Warrant ETF"];
	$rootScope.region = ["United States of America","United Kingdom","Japan","Argentina","Australia","New Zealand","Canada","India","Benelux","Brazil","France","Germany/Austria","Greater China","Iberia","Italy","Mexico","Middle East","Nordic","Other Asia","Other Europe","Other Latin America","Russia/CIS","South Korea","Southeast Asia","Switzerland","Taiwan","Turkey","CEE","Emerging Europe","GHQ","South Africa"];
	$rootScope.categories = ["Cash","Agency Debt","Bond"];
	$rootScope.conditions = ["Illegality","Credit Event Upon Merger","Additional Termination Events"];
    demoApp.openModal = () => {
        const modalInstance = $uibModal.open({
            templateUrl: 'initiateAgreementModal.html',
            controller: 'ModalInstanceCtrl',
            controllerAs: 'modalInstance',
            resolve: {
                peers: () => $rootScope.peers
            }
        });
        modalInstance.result.then(() => {}, () => {});
    };

	demoApp.viewAgreement = (agreement, displayAmendAgree) => {
		$rootScope.showAmendAgree = displayAmendAgree;
		const modalInstance = $uibModal.open({
			templateUrl: 'viewAgreementModal.html',
			controller: 'ViewAgreementCtrl',
			controllerAs: 'ViewAgreementCtrl',
			resolve: {
				agreement: () => agreement
			}
		});
		modalInstance.result.then(() => {}, () => {});
    };

	demoApp.amendAgreement = (agreement) => {
        const modalInstance = $uibModal.open({
            templateUrl: 'amendAgreementModal.html',
            controller: 'AmendAgreementCtrl',
            controllerAs: 'AmendAgreementCtrl',
            resolve: {
                agreement: () => agreement,
				deliveryAmount: () => $rootScope.deliveryAmount,
				collateral: () => $rootScope.collateral,
				threshold: () => $rootScope.threshold,
				peers: () => $rootScope.peers,
				yesNo: () => $rootScope.yesNo,
				allParties: () => allParties
            }
        });

        modalInstance.result.then(() => {}, () => {});
    };


	demoApp.viewAudit = (agreement) => {
        $http.get($rootScope.apiBaseURL + "audit?agreementName=" + agreement.agrementName)
        .then(function(response) {
			demoApp.agreementAudits = response.data;
		});
    };

	demoApp.getAgreements = () => $http.get($rootScope.apiBaseURL + "getAgreements")
        .then(function(response) {
			demoApp.agreements = response.data;
		});
	//demoApp.agreements = [{id:1}]; //SanjayTest


    demoApp.getAgreements();
	$scope.getDateValue = (dt) => {
		var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
		if (dt != null && dt != undefined && dt != '') {
			var createdDate = new Date(dt);
			var day = createdDate.getDate();
			var month = createdDate.getMonth();
			var year = createdDate.getFullYear();
			var time = createdDate.toLocaleTimeString().replace(/(.*)\D\d+/, '$1');

			var formattedDate = day + '-' + months[month] + '-' + year;
			return formattedDate;
		} else {
			return 'NA';
		}
    };
	$scope.getTimeValue = (dt) => {
		if (dt != null && dt != undefined && dt != '') {
			var createdDate = new Date(dt);
			var time = createdDate.toLocaleTimeString().replace(/(.*)\D\d+/, '$1');
			//var time = createdDate.toLocaleTimeString('en-US', {hour: '2-digit', minute:'2-digit'});
			return time;
		} else {
			return 'NA';
		}
    };
	$rootScope.convertToDt = (str) => {
		if(str instanceof Date)
		{

		}
		else {
			if (str != null && str != undefined && str != '') {
				var createdDate = new Date(str);
				return createdDate;
			} else {
				return new Date();
			}
		}
    };
});

app.controller('ModalInstanceCtrl', function ($scope, $rootScope, $http, $location,
    $uibModalInstance, $uibModal, peers) {
    const modalInstance = this;

	$scope.moodys = $rootScope.moodys;
	$scope.sps = $rootScope.sps;
	$scope.fitchs = $rootScope.fitchs;

    modalInstance.peers = peers;
	modalInstance.allParties = $rootScope.allParties;
    modalInstance.form = {baseCurrency: 'GBP',valuationPercentageCash:100};
    $scope.agreement = {baseCurrency: 'GBP',valuationPercentageCash:100, eligibleCollaterals: [], thresholds: [],specifiedConditions: []};
    modalInstance.formError = false;
	modalInstance.deliveryAmount = $rootScope.deliveryAmount;
	modalInstance.collateral = $rootScope.collateral;
	modalInstance.threshold = $rootScope.threshold;
	modalInstance.yesNo = $rootScope.yesNo;
	modalInstance.currencies = $rootScope.currencies;
	modalInstance.products = $rootScope.products;
	$scope.cond1 = false;
	$scope.cond2 = false;
	$scope.cond3 = false;
	$scope.cp = ["Counterparty, New York","Firm, London"];


	$scope.getCurrencies = (currencies) => {
		var text = "";
		for (var i = 0; i < currencies.length; i++) {
			text += currencies[i];
			if (i != currencies.length-1) {
				text += ", ";
			}
		}
		return text;
    };

	$scope.changedValue = () => {
			const secCtrlInstance = $uibModal.open({
				templateUrl: 'eligibleColNew.html',
				controller: 'eligibleCollateralCtrl',
				controllerAs: 'eligibleCollateralCtrl',
				resolve: {
					apiBaseURL: () => $rootScope.apiBaseURL,
					agreementModel: () => $scope.agreement,
					data: () => null
				}
			});
			secCtrlInstance.result.then(() => {}, () => {});

	}

	$scope.addThresholds = () => {
		const thresholdsCtrlInstance = $uibModal.open({
            templateUrl: 'addThresholds.html',
            controller: 'thresholdsCtrl',
            controllerAs: 'thresholdsCtrl',
            resolve: {
                apiBaseURL: () => $rootScope.apiBaseURL,
				agreementModel: () => $scope.agreement,
				data: () => null
            }
        });
		thresholdsCtrlInstance.result.then(() => {}, () => {});
	}
	$scope.addConditions = (id) => {
		if(id == 1){
			var ind = $rootScope.agreement.specifiedConditions.indexOf("Illegality");
			if($scope.cond1)
				$scope.agreement.specifiedConditions.push("Illegality");
			else{
				if(ind != -1)
					$scope.agreement.specifiedConditions.splice(ind, 1);
			}
		}
		else if(id ==2){
			var ind = $scope.agreement.specifiedConditions.indexOf("Credit Event Upon Merger");
			if($scope.cond2)
				$scope.agreement.specifiedConditions.push("Credit Event Upon Merger");
			else{
				if(ind != -1)
					$scope.agreement.specifiedConditions.splice(ind, 1);
			}
		}
		else if(id ==3){
			var ind = $scope.agreement.specifiedConditions.indexOf("Additional Termination Events");
			if($scope.cond3)
				$scope.agreement.specifiedConditions.push("Additional Termination Events");
			else{
				if(ind != -1)
					$scope.agreement.specifiedConditions.splice(ind, 1);
			}
		}

	}
    // Validate and create IOU.
    modalInstance.create = () => {
		console.log('Called Create 4' + JSON.stringify($scope.agreement));
		modalInstance.formError = false;
		$uibModalInstance.close();
		var agreement = {
			agrementName:$scope.agreement.agrementName,
			counterparty:$scope.agreement.counterparty,
			baseCurrency:$scope.agreement.baseCurrency,
			eligibleCurrency:$scope.agreement.eligibleCurrency,
			products:$scope.agreement.products,
			deliveryAmount:$scope.agreement.deliveryAmount,
			returnAmount:$scope.agreement.returnAmount,
			creditSupportAmount:$scope.agreement.creditSupportAmount,

			eligibleCollaterals:$scope.agreement.eligibleCollaterals,
			thresholds:$scope.agreement.thresholds,
			specifiedConditions:$scope.agreement.specifiedConditions,

			initialMargin:$scope.agreement.initialMargin,
			valuationAgent:$scope.agreement.valuationAgent,
			valuationDate:$scope.agreement.valuationDate,
			valuationTime:$scope.agreement.valuationTime,
			notificationTime:$scope.agreement.notificationTime,
			substitutionDateFrom:$scope.agreement.substitutionDateFrom,
			substitutionDateTo:$scope.agreement.substitutionDateTo,
			consent:$scope.agreement.consent

		};
		//$rootScope.dummy = agreement;//SanjayTest
		console.log(agreement);
		const createIOUEndpoint = $rootScope.apiBaseURL +  "initFlow";

		// Create PO and handle success / fail responses.
		$http.post(createIOUEndpoint, angular.toJson(agreement)).then(
			(result) => modalInstance.displayMessage(result, agreement),
			(result) => modalInstance.displayMessage(result, agreement),

		);

    };

    modalInstance.displayMessage = (message, agreement) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContentInit.html',
            controller: 'messageCtrlInit',
            controllerAs: 'modalInstanceInit',
            resolve: { message: () => message,  agreement: () => agreement}
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create IOU modal dialogue.
    modalInstance.cancel = () => $uibModalInstance.dismiss();

    // Validate the IOU.
    function invalidFormInput() {
        return ($scope.agreement.counterparty === undefined);
    }
});

app.controller('messageCtrlInit', function ($uibModalInstance, message, agreement) {
    const modalInstanceInit = this;
    //modalInstanceInit.message = message.data;
	modalInstanceInit.message = "You have initiated the agreement '"+agreement.agrementName+"'. Parties can now amend the terms untill all parties agrees.";
});

app.controller('messageCtrlAmend', function ($uibModalInstance, message, agreement, $rootScope) {
    const modalInstanceAmend = this;
    //modalInstanceInit.message = message.data;
	var otherParty = $rootScope.thisNode == agreement.cptyInitiator ? agreement.counterparty : agreement.cptyInitiator;
	modalInstanceAmend.message = "You have amended the agreement '"+agreement.agrementName+"'. Parties can now amend the terms untill all parties agrees.";
});


app.controller('ViewAgreementCtrl', function ($scope, $rootScope, $http, $location, $uibModalInstance, $uibModal, agreement) {
    const modalInstance = this;
    $scope.agreement = agreement;
	$scope.currencies = $rootScope.currencies;
	$scope.products = $rootScope.products;
	$scope.isAgreeAllowed = true;

	if ((agreement.pendingParticipants && agreement.pendingParticipants.indexOf($rootScope.thisNode) === -1)) {
		$scope.isAgreeAllowed = false;
	}

	// Validate and create IOU.
	$scope.amendAgreement = (agreement) => {
        const modalInstance1 = $uibModal.open({
            templateUrl: 'amendAgreementModal.html',
            controller: 'AmendAgreementCtrl',
            controllerAs: 'AmendAgreementCtrl',
            resolve: {
                agreement: () => agreement,
				deliveryAmount: () => $rootScope.deliveryAmount,
				collateral: () => $rootScope.collateral,
				threshold: () => $rootScope.threshold,
				peers: () => $rootScope.peers,
				yesNo: () => $rootScope.yesNo,
				allParties: () => $rootScope.allParties
            }
        });
		modalInstance1.ag = agreement;

        modalInstance1.result.then(() => {
			console.log('Came here1 ',modalInstance1.ag);
		}, () => {
			console.log('Came here2');
		});
    };
	$scope.getCurrencies = (currencies) => {
		console.log('Came here2'+currencies);
		var text = "";
		for (var i = 0; i < currencies.length; i++) {
			text += currencies[i] + " ";
		}
		return text;
    };

	$scope.hasValueChanged = (field, type, value) => {
		var fieldVal = $scope.agreement.changedFields[field];
		if(fieldVal != null && fieldVal != undefined){
			if(type == 1){//It is an array
				for (var i = 0; i < fieldVal.length; i++) {
					var element = fieldVal[i];
					var index = $scope.agreement[field].indexOf(element);
					if(index != -1)
						return true;
				}
			} if(type == 2){//It is a value
				var currentVal = $scope.agreement[field];
				if(currentVal.indexOf(value) == -1 && fieldVal.indexOf(value) != -1)
					return true;
				if(currentVal.indexOf(value) != -1 && fieldVal.indexOf(value) == -1)
					return true;
			} else {
				return fieldVal != $scope.agreement[field];
			}
		}
		return false;
    };

    $scope.agree = (agreement) => {
		console.log('Called Agree '+agreement);
		var updAgreement ={
			agrementName: agreement.agrementName,
			agreementValue: agreement.agreementValue,
			collateral: agreement.collateral
		}
		$uibModalInstance.close();
		const createIOUEndpoint = $rootScope.apiBaseURL + "acceptFlow";
		// Create PO and handle success / fail responses.
		$http.put(createIOUEndpoint, angular.toJson(updAgreement)).then(
			(result) => $scope.displayMessage(result, agreement),
			(result) => $scope.displayMessage(result, agreement)
		);
		//$scope.cancel();
    };
	$scope.getAmtValue = (id) => {
		if(id != 0){
			var v = $rootScope.deliveryAmount.find(x => x.id === id);
			if(v != null)
				return v.name;
		}
		return "";
    };
	$scope.getDateValue = (dt) => {
		var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
		if (dt != null && dt != undefined && dt != '') {
			var createdDate = new Date(dt);
			var day = createdDate.getDate();
			var month = createdDate.getMonth();
			var year = createdDate.getFullYear();
			var time = createdDate.toLocaleTimeString().replace(/(.*)\D\d+/, '$1');

			var formattedDate = day + '-' + months[month] + '-' + year;
			return formattedDate;
		} else {
			return 'NA';
		}
    };
	$scope.getTimeValue = (dt) => {
		if (dt != null && dt != undefined && dt != '') {
			var createdDate = new Date(dt);
			var time = createdDate.toLocaleTimeString().replace(/(.*)\D\d+/, '$1');
			//var time = createdDate.toLocaleTimeString('en-US', {hour: '2-digit', minute:'2-digit'});
			return time;
		} else {
			return 'NA';
		}
    };

    $scope.displayMessage = (message, agreement) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message , agreement: () => agreement }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create IOU modal dialogue.
    $scope.cancel = () => $uibModalInstance.dismiss();
});

app.controller('AmendAgreementCtrl', function ($scope, $rootScope, $http, $location, $uibModalInstance, $uibModal, agreement,deliveryAmount,collateral,threshold,peers,yesNo, allParties) {
    const modalInstance = this;
	$scope.deliveryAmount=deliveryAmount;
	$scope.collateral= collateral;
	$scope.threshold= threshold;
	$scope.peers= peers;
	$scope.allParties = allParties;
	$scope.yesNo= yesNo;
	$scope.currencies = $rootScope.currencies;
	$scope.products = $rootScope.products;
	$scope.allCounterParties = [];

	for (var i = 0; i < $scope.allParties.length; i++) {
		if ($scope.allParties[i] != agreement.cptyInitiator)
		{
			$scope.allCounterParties.push(allParties[i]);
		}
	}

	//$scope.agreement = $rootScope.dummy; //SanjayTest
    $scope.agreement = agreement;
	$scope.agreement.substitutionDateFrom = $rootScope.convertToDt($scope.agreement.substitutionDateFrom);
	$scope.agreement.substitutionDateTo = $rootScope.convertToDt($scope.agreement.substitutionDateTo);
	$scope.cond1 = $scope.agreement.specifiedConditions.indexOf('Illegality') > -1;;
	$scope.cond2 = $scope.agreement.specifiedConditions.indexOf('Credit Event Upon Merger') > -1;;
	$scope.cond3 = $scope.agreement.specifiedConditions.indexOf('Additional Termination Events') > -1;
	// Validate and create IOU.
    $scope.amendAgreement = (agreement) => {
		console.log('Called Amend ',agreement);
		const updatedAgreement = {
			agrementName: agreement.agrementName,
			agreementValue: agreement.agreementValue,
			collateral: agreement.collateral
		};

		$uibModalInstance.close();
		const createIOUEndpoint = $rootScope.apiBaseURL + "amendFlow";
		// Create PO and handle success / fail responses.
		$http.put(createIOUEndpoint, angular.toJson(agreement)).then(
			(result) => $scope.displayMessage(result, agreement),
			(result) => $scope.displayMessage(result, agreement)
		);
		//$scope.cancel();
    };
	$scope.displayMessage = (message, agreement) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContentAmend.html',
            controller: 'messageCtrlAmend',
            controllerAs: 'modalInstanceAmend',
            resolve: { message: () => message, agreement: () => agreement }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };
	$scope.collateralType = 0;
	$scope.changedValue = () => {
		const secCtrlInstance = $uibModal.open({
			templateUrl: 'eligibleColNew.html',
			controller: 'eligibleCollateralCtrl',
			controllerAs: 'eligibleCollateralCtrl',
			resolve: {
				apiBaseURL: () => $rootScope.apiBaseURL,
				agreementModel: () => $scope.agreement,
				data: () => null
			}
		});
		secCtrlInstance.result.then(() => {}, () => {});
	}
	$scope.addThresholds = () => {
		const thresholdsCtrlInstance = $uibModal.open({
            templateUrl: 'addThresholds.html',
            controller: 'thresholdsCtrl',
            controllerAs: 'thresholdsCtrl',
            resolve: {
                apiBaseURL: () => $rootScope.apiBaseURL,
				agreementModel: () => $scope.agreement,
				data: () => null
            }
        });
		thresholdsCtrlInstance.result.then(() => {}, () => {});
	}

	$scope.editCollateral = (data) => {
		if(data.collateralType == 1){
			const cashCtrlInstance = $uibModal.open({
				templateUrl: 'eligibleColCashUpd.html',
				controller: 'cashUpdCtrl',
				controllerAs: 'cashUpdCtrl',
				resolve: {
					apiBaseURL: () => $rootScope.apiBaseURL,
					agreementModel: () => $scope.agreement,
					data: () => data
				}
			});
			cashCtrlInstance.result.then(() => {}, () => {});
		} else {
			const secCtrlInstance = $uibModal.open({
				templateUrl: 'eligibleColSecUpd.html',
				controller: 'secUpdCtrl',
				controllerAs: 'secUpdCtrl',
				resolve: {
					apiBaseURL: () => $rootScope.apiBaseURL,
					agreementModel: () => $scope.agreement,
					data: () => data
				}
			});
			secCtrlInstance.result.then(() => {}, () => {});
		}
    };
	$scope.deleteCollateral = (item) => {

		var index = $scope.agreement.eligibleCollaterals.indexOf(item);
		if(index != -1)
			$scope.agreement.eligibleCollaterals.splice(index, 1);

    };

	$scope.editThresholds = (data) => {
        const thresholdsCtrlInstance = $uibModal.open({
            templateUrl: 'addThresholdsUpd.html',
            controller: 'thresholdsUpdCtrl',
            controllerAs: 'thresholdsUpdCtrl',
            resolve: {
                apiBaseURL: () => $rootScope.apiBaseURL,
				agreementModel: () => $scope.agreement,
				data: () => data
            }
        });
		thresholdsCtrlInstance.result.then(() => {}, () => {});
    };
	$scope.deleteThreshold = (item) => {
        var index = $scope.agreement.thresholds.indexOf(item);
		if(index != -1)
			$scope.agreement.thresholds.splice(index, 1);
    };

	$scope.addConditions = (id) => {

		if(id == 1){
			var ind = $scope.agreement.specifiedConditions.indexOf("Illegality");
			if($scope.cond1)
				$scope.agreement.specifiedConditions.push("Illegality");
			else{
				if(ind != -1)
					$scope.agreement.specifiedConditions.splice(ind, 1);
			}
		}
		else if(id ==2){
			var ind = $scope.agreement.specifiedConditions.indexOf("Credit Event Upon Merger");
			if($scope.cond2)
				$scope.agreement.specifiedConditions.push("Credit Event Upon Merger");
			else{
				if(ind != -1)
					$scope.agreement.specifiedConditions.splice(ind, 1);
			}
		}
		else if(id ==3){
			var ind = $scope.agreement.specifiedConditions.indexOf("Additional Termination Events");
			if($scope.cond3)
				$scope.agreement.specifiedConditions.push("Additional Termination Events");
			else{
				if(ind != -1)
					$scope.agreement.specifiedConditions.splice(ind, 1);
			}
		}
	}
	$scope.getCond = (id) => {
		if(id == 1){
			$scope.cond1 = $scope.agreement.specifiedConditions.indexOf('Illegality') > -1;
			return $scope.cond1;
		}
		else if(id ==2){
			$scope.cond2 = $scope.agreement.specifiedConditions.indexOf('Credit Event Upon Merger') > -1;
			return $scope.cond2;
		}
		else if(id ==3){
			$scope.cond3 = $scope.agreement.specifiedConditions.indexOf('Additional Termination Events') > -1;
			return $scope.cond3;
		}
	}

    // Close create IOU modal dialogue.
    $scope.cancel = () => $uibModalInstance.dismiss();
});

// Controller for success/fail modal dialogue.
app.controller('messageCtrl', function ($uibModalInstance, message, agreement) {
    const modalInstanceTwo = this;
	var msg = message.data.status == "Agreed" ? "All parties are now agreed on '"+agreement.agrementName+"'."
	: "You are agreed on "+agreement.agrementName+"', please wait for others to approve the agreement.";
     modalInstanceTwo.message = msg;
});


app.controller('cashCtrl', function ($scope,$rootScope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, agreementModel, data) {

    $scope.el = {collateralType: 1, currency: "", moody: "",sp: "",fitch:"",rfrom:"",rto:"",valuation:100,remMaturity:"",remMaturityFrom:"",remMaturityTo:"",partyA: true,partyB: true};
	$scope.elNew = {collateralType: 1, currency: "", moody: "",sp: "",fitch:"",rfrom:"",rto:"",valuation:100,remMaturity:"",remMaturityFrom:"",remMaturityTo:"",partyA: true,partyB: true};
	$scope.yesNo = $rootScope.yesNo;
	$scope.currencies = $rootScope.currencies;
	$scope.products = $rootScope.products;

	$scope.cash = [];
	if(data != null){
		$scope.el.currency = data.currency;
		$scope.el.valuation = data.amount;
		$scope.el.partyA = data.partyA === 1?true:false;
		$scope.el.partyB = data.partyB === 1?true:false;
	}

	$scope.submit = (el1) => {
		console.log('Called Create '+el);
		//$scope.cash.push(el);
		//agreementModel.eligibleCollaterals.push(el);
		for(var i = 0;i< $scope.cash.length;i++){
			var el = $scope.cash[i];
			var eligibleCash = {
				collateralType: 1,
				currency: el.currency,
				ratingType: 0,
				rating: 0,
				ratingText: "",
				ratingRangeFrom: 0,
				ratingRangeTo: 0,
				amount: el.valuation,
				remainingMaturity: 0,
				remMaturityFrom: 0,
				remMaturityTo: 0,
				partyA: el.partyA ? 1:0,
				partyB: el.partyB ? 1:0
			}
			console.log('Adding Cash EC ',eligibleCash);
			agreementModel.eligibleCollaterals.push(eligibleCash);
		}
		$uibModalInstance.dismiss();
    };
	$scope.add = (el) => {
		console.log('1');
		$scope.cash.push(el);
		/*var eligibleCash = {
			collateralType: 1,
			currency: el.currency,
			ratingType: 0,
			rating: 0,
			ratingText: "",
			ratingRangeFrom: 0,
			ratingRangeTo: 0,
			amount: el.valuation,
			remainingMaturity: 0,
			remMaturityFrom: 0,
			remMaturityTo: 0,
			partyA: el.partyA ? 1:0,
			partyB: el.partyB ? 1:0
		}
		console.log('Adding Cash EC ',eligibleCash);
		agreementModel.eligibleCollaterals.push(eligibleCash);*/
		$scope.el = {collateralType: 1, currency: "", moody: "",sp: "",fitch:"",rfrom:"",rto:"",valuation:100,remMaturity:"",remMaturityFrom:"",remMaturityTo:"",partyA: true,partyB: true};
    };
	$scope.cancel = () => {
		$uibModalInstance.dismiss();
    };

	$scope.deleteCashColl = (x) => {
		var ind = $scope.cash.indexOf(x);
		if(ind != -1)
			$scope.cash.splice(ind,1);
		var ind1 = agreementModel.eligibleCollaterals.indexOf(x);
		if(ind1 != -1)
			agreementModel.eligibleCollaterals.splice(ind1, 1);
    };
});

app.controller('cashUpdCtrl', function ($scope,$rootScope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, agreementModel, data) {

    $scope.el = {collateralType: 1, currency: "", moody: "",sp: "",fitch:"",rfrom:"",rto:"",valuation:100,remMaturity:"",remMaturityFrom:"",remMaturityTo:"",partyA: true,partyB: true};
	$scope.elNew = {collateralType: 1, currency: "", moody: "",sp: "",fitch:"",rfrom:"",rto:"",valuation:100,remMaturity:"",remMaturityFrom:"",remMaturityTo:"",partyA: true,partyB: true};
	$scope.yesNo = $rootScope.yesNo;
	$scope.currencies = $rootScope.currencies;
	$scope.products = $rootScope.products;

	$scope.cash = [];
	if(data != null){
		$scope.el.currency = data.currency;
		$scope.el.valuation = data.amount;
		$scope.el.partyA = data.partyA === 1?true:false;
		$scope.el.partyB = data.partyB === 1?true:false;
	}
	//var index = $scope.agreement.eligibleCollaterals.indexOf(data);
	$scope.submit = (el) => {
		console.log('Called Create '+el);
		for(var i = 0;i< $scope.cash.length;i++){
			var el = $scope.cash[i];
			var eligibleCash = {
				collateralType: 1,
				currency: el.currency,
				ratingType: 0,
				rating: 0,
				ratingText: "",
				ratingRangeFrom: 0,
				ratingRangeTo: 0,
				amount: el.valuation,
				remainingMaturity: 0,
				remMaturityFrom: 0,
				remMaturityTo: 0,
				partyA: el.partyA ? 1:0,
				partyB: el.partyB ? 1:0
			}
			agreementModel.eligibleCollaterals.push(eligibleCash);
		}
		$uibModalInstance.dismiss();
    };
	$scope.addCash = (el) => {
		$scope.cash.push(el);
		/*var eligibleCash = {
			collateralType: 1,
			currency: el.currency,
			ratingType: 0,
			rating: 0,
			ratingText: "",
			ratingRangeFrom: 0,
			ratingRangeTo: 0,
			amount: el.valuation,
			remainingMaturity: 0,
			remMaturityFrom: 0,
			remMaturityTo: 0,
			partyA: el.partyA ? 1:0,
			partyB: el.partyB ? 1:0
		}
		//agreementModel.eligibleCollaterals.push(eligibleCash);*/
		//$uibModalInstance.dismiss();
		$scope.el = {collateralType: 1, currency: "", moody: "",sp: "",fitch:"",rfrom:"",rto:"",valuation:100,remMaturity:"",remMaturityFrom:"",remMaturityTo:"",partyA: true,partyB: true};
    };
	$scope.cancel = () => {
		$uibModalInstance.dismiss();
    };
});

app.controller('eligibleCollateralCtrl', function ($scope, $rootScope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, agreementModel, data) {
    $scope.el = {region: "",category: "",qualifier: "", currencies:"", moodysMax: "",moodysMin: "",spMax: "",spMin: "",fitchMax: "",fitchMin: "",remMaturity: "",ranges: [],partyA: true,partyB: true};
	$scope.moodys = $rootScope.moodys;
	$scope.sps = $rootScope.sps;
	$scope.fitchs = $rootScope.fitchs;
	$scope.currencies = $rootScope.currencies;
	$scope.products = $rootScope.products;
	$scope.showEditFields = false;
	$scope.region = $rootScope.region;
	$scope.categories = $rootScope.categories;
	$rootScope.moodysNew = ["AAA","Aa1","Aa2","Aa3","A1","A2","A3","Baa1","Baa2","Baa3"];
	$rootScope.spNew = ["AAA","AA+","AA","AA-","A+","A","A-","BBB+","BBB","BBB-"];
	$rootScope.fitchNew = ["AAA","AA+","AA","AA-","A+","A","A-","BBB+","BBB","BBB-"];
	$rootScope.maturities = ["Remaining","Original"];

	$scope.elNew = {collateralType: 2, currency: "", moody: "",sp: "",fitch:"",rfrom:0,rto:0,valuation:0,remMaturity:"No",remMaturityFrom:"",remMaturityTo:"",partyA: true,partyB: true};
	$scope.yesNo = $rootScope.yesNo;

	$scope.getCurrencies = (currencies) => {
		var text = "";
		for (var i = 0; i < currencies.length; i++) {
			text += currencies[i];
			if (i != currencies.length-1) {
				text += ", ";
			}
		}
		return text;
    };

	$scope.sec = [];
	if(data != null){
		$scope.el.rfrom = data.ratingRangeFrom;
		$scope.el.rto = data.ratingRangeTo;
		$scope.el.valuation = data.amount;
		$scope.el.remMaturity = data.remainingMaturity;
		$scope.el.remMaturityFrom = data.remMaturityFrom;
		$scope.el.remMaturityTo = data.remMaturityTo;
		$scope.el.partyA = data.partyA === 1?true:false;
		$scope.el.partyB = data.partyB === 1?true:false;
		$scope.el.currencies = data.currencies;
	}
	$scope.edit = () => {
		console.log('called ');
		$scope.showEditFields = !$scope.showEditFields;

	}
	$scope.range = {
		rangeFrom: "",
		rangeTo: "",
		valuation: ""
	};
	$scope.ranges = [];
	$scope.addRange = (range, el) => {
		var rangeRow = {
			rangeFrom: range.rangeFrom,
			rangeTo: range.rangeTo,
			valuation: range.valuation
		}
		el.ranges.push(rangeRow);
		$scope.ranges.push(rangeRow);
		$scope.range.rangeFrom = "";
		$scope.range.rangeTo = "";
		$scope.range.valuation = "";
	}

	$scope.submit = (el1) => {
		for(var i = 0;i< $scope.sec.length;i++){
			var el = $scope.sec[i];
			var eligibleSec = {
				region: el.region,
				category: el.category,
				qualifier: el.qualifier,
				currencies: el.currencies,
				moodysMax: el.moodysMax,
				moodysMin: el.moodysMin,
				spMax: el.spMax,
				spMin: el.spMin,
				fitchMax: el.fitchMax,
				fitchMin: el.fitchMin,
				remMaturity: el.remMaturity,
				ranges: el.ranges,
				partyA: el.partyA,
				partyB: el.partyB
			}
			console.log('Adding Security EC ',eligibleSec);
			agreementModel.eligibleCollaterals.push(eligibleSec);
		}
		$uibModalInstance.dismiss();
    };
	$scope.add = (el) => {
		$scope.sec.push(el);
		$scope.el = {region: "",category: "",qualifier: "", currencies: "", moodysMax: "",moodysMin: "",spMax: "",spMin: "",fitchMax: "",fitchMin: "",remMaturity: "",ranges: [],partyA: true,partyB: true};
		$scope.ranges = [];
		$scope.range.rangeFrom = "";
		$scope.range.rangeTo = "";
		$scope.range.valuation = "";
    };
	$scope.cancel = () => {
		$uibModalInstance.dismiss();
    };
	$scope.deleteSecColl = (x) => {
		var ind = $scope.sec.indexOf(x);
		if(ind != -1)
			$scope.sec.splice(ind,1);
		var ind1 = agreementModel.eligibleCollaterals.indexOf(x);
		if(ind1 != -1)
			agreementModel.eligibleCollaterals.splice(ind1, 1);
    };
});

app.controller('secUpdCtrl', function ($scope, $rootScope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, agreementModel, data) {
    $scope.el = {collateralType: 2, currency: "", moody: "",sp: "",fitch:"",rfrom:0,rto:0,valuation:0,remMaturity:0,remMaturityFrom:0,remMaturityTo:0,partyA: true,partyB: true};
	$scope.moodys = $rootScope.moodys;
	$scope.sps = $rootScope.sps;
	$scope.fitchs = $rootScope.fitchs;
	$scope.currencies = $rootScope.currencies;

	$scope.elNew = {collateralType: 2, currency: "", moody: "",sp: "",fitch:"",rfrom:0,rto:0,valuation:0,remMaturity:"No",remMaturityFrom:"",remMaturityTo:"",partyA: true,partyB: true};
	$scope.yesNo = $rootScope.yesNo;

	$scope.sec = [];
	if(data != null){
		$scope.el.rfrom = data.ratingRangeFrom;
		$scope.el.rto = data.ratingRangeTo;
		$scope.el.valuation = data.amount;
		$scope.el.remMaturity = data.remainingMaturity;
		$scope.el.remMaturityFrom = data.remMaturityFrom;
		$scope.el.remMaturityTo = data.remMaturityTo;
		$scope.el.partyA = data.partyA === 1?true:false;
		$scope.el.partyB = data.partyB === 1?true:false;
	}


	$scope.submit = (el1) => {
		for(var i = 0;i< $scope.sec.length;i++){
			var el = $scope.sec[i];
			var eligibleSec = {
				collateralType: 2,
				currency: el.currency,
				ratingType: (el.moody != "" ? 1:(el.sp != "" ? 2:(el.fitch != ""?3:0))),
				rating: (el.moody != "" ? el.moody.id:(el.sp != "" ? el.sp.id:el.fitch.id)),
				ratingText: (el.moody != "" ? el.moody.name:(el.sp != "" ? el.sp.name:el.fitch.name)),
				ratingRangeFrom: el.rfrom,
				ratingRangeTo: el.rto,
				amount: el.valuation,
				remainingMaturity: el.remMaturity,
				remMaturityFrom: el.remMaturityFrom,
				remMaturityTo: el.remMaturityTo,
				partyA: el.partyA ? 1:0,
				partyB: el.partyB ? 1:0
			}
			console.log('Adding Security EC ',eligibleSec);
			agreementModel.eligibleCollaterals.push(eligibleSec);
		}
		$uibModalInstance.dismiss();
    };
	$scope.addSec = (el) => {
		$scope.sec.push(el);
		/*var eligibleSec = {
			collateralType: 2,
			currency: el.currency,
			ratingType: (el.moody != "" ? 1:(el.sp != "" ? 2:(el.fitch != ""?3:0))),
			rating: (el.moody != "" ? el.moody.id:(el.sp != "" ? el.sp.id:el.fitch.id)),
			ratingText: (el.moody != "" ? el.moody.name:(el.sp != "" ? el.sp.name:el.fitch.name)),
			ratingRangeFrom: el.rfrom,
			ratingRangeTo: el.rto,
			amount: el.valuation,
			remainingMaturity: el.remMaturity,
			remMaturityFrom: el.remMaturityFrom,
			remMaturityTo: el.remMaturityTo,
			partyA: el.partyA ? 1:0,
			partyB: el.partyB ? 1:0
		}
		console.log('Adding Security EC ',eligibleSec);
		$uibModalInstance.dismiss();
		agreementModel.eligibleCollaterals.push(eligibleSec);*/
		$scope.el = {collateralType: 2, currency: "", moody: "",sp: "",fitch:"",rfrom:0,rto:0,valuation:0,remMaturity:0,remMaturityFrom:0,remMaturityTo:0,partyA: true,partyB: true};
    };
	$scope.cancel = () => {
		$uibModalInstance.dismiss();
    };
});

app.controller('thresholdsCtrl', function ($scope, $rootScope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, agreementModel, data) {

	$scope.el = {region: "",category: "",qualifier: "", currencies: "",amount:"", moodysMax: "",moodysMin: "",spMax: "",spMin: "",fitchMax: "",fitchMin: "",partyA: true,partyB: true};
	$scope.moodys = $rootScope.moodys;
	$scope.sps = $rootScope.sps;
	$scope.fitchs = $rootScope.fitchs;
	$scope.currencies = $rootScope.currencies;
	$scope.products = $rootScope.products;
	$scope.showEditFields = false;
	$scope.region = $rootScope.region;
	$scope.categories = $rootScope.categories;

	$rootScope.moodysNew = ["AAA","Aa1","Aa2","Aa3","A1","A2","A3","Baa1","Baa2","Baa3"];
	$rootScope.spNew = ["AAA","AA+","AA","AA-","A+","A","A-","BBB+","BBB","BBB-"];
	$rootScope.fitchNew = ["AAA","AA+","AA","AA-","A+","A","A-","BBB+","BBB","BBB-"];
	$rootScope.maturities = ["Remaining","Original"];

	$scope.sec = [];

	if(data != null){
		$scope.el.rfrom = data.ratingRangeFrom;
		$scope.el.rto = data.ratingRangeTo;
		$scope.el.amount = data.amount;
		$scope.el.partyA = data.partyA === 1?true:false;
		$scope.el.partyB = data.partyB === 1?true:false;
	}

	$scope.submit = (el1) => {
		for(var i = 0;i< $scope.sec.length;i++){
			var el = $scope.sec[i];
			var threshold = {
				region: el.region,
				category: el.category,
				qualifier: el.qualifier,
				moodysMax: el.moodysMax,
				moodysMin: el.moodysMin,
				spMax: el.spMax,
				spMin: el.spMin,
				fitchMax: el.fitchMax,
				fitchMin: el.fitchMin,
				amount: el.amount,
				currencies: el.currencies,
				partyA: el.partyA,
				partyB: el.partyB
			}
			console.log('Submit thresholdsCtrl',threshold);
			agreementModel.thresholds.push(threshold);
		}
		$uibModalInstance.dismiss();
    };

	$scope.add = (el) => {
		console.log('3');
		$scope.sec.push(el);
		$scope.el = {region: "",category: "",qualifier: "", currencies: "", amount:"", moodysMax: "",moodysMin: "",spMax: "",spMin: "",fitchMax: "",fitchMin: "",partyA: true,partyB: true};
    };
	$scope.deleteSecColl = (x) => {
		var ind = $scope.sec.indexOf(x);
		if(ind != -1)
			$scope.sec.splice(ind,1);
		var ind1 = agreementModel.thresholds.indexOf(x);
		if(ind1 != -1)
			agreementModel.thresholds.splice(ind1, 1);
    };
	$scope.cancel = () => {
		$uibModalInstance.dismiss();
    };
});

app.controller('thresholdsUpdCtrl', function ($scope, $rootScope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, agreementModel, data) {
    $scope.el = {moody: "",sp: "",fitch:"",rfrom:0,rto:0,valuation:0,partyA: true,partyB: true};
	$scope.moodys = $rootScope.moodys;
	$scope.sps = $rootScope.sps;
	$scope.fitchs = $rootScope.fitchs;
	$scope.yesNo = $rootScope.yesNo;
	$scope.currencies = $rootScope.currencies;

	$scope.sec = [];

	if(data != null){
		$scope.el.rfrom = data.ratingRangeFrom;
		$scope.el.rto = data.ratingRangeTo;
		$scope.el.valuation = data.amount;
		$scope.el.partyA = data.partyA === 1?true:false;
		$scope.el.partyB = data.partyB === 1?true:false;
	}

	$scope.submit = (el1) => {
		for(var i = 0;i< $scope.sec.length;i++){
			var el = $scope.sec[i];
			var threshold = {
				collateralType: 0,
				currency: "",
				ratingType: (el.moody != "" ? 1:(el.sp != "" ? 2:(el.fitch != ""?3:0))),
				rating: (el.moody != "" ? el.moody.id:(el.sp != "" ? el.sp.id:(el.fitch != "" ? el.fitch.id: ""))),
				ratingText: (el.moody != "" ? el.moody.name:(el.sp != "" ? el.sp.name:(el.fitch != "" ? el.fitch.name: ""))),
				ratingRangeFrom: el.rfrom,
				currencies: "",
				ratingRangeTo: el.rto,
				amount: el.valuation,
				remainingMaturity: 0,
				remMaturityFrom: 0,
				remMaturityTo: 0,
				partyA: el.partyA ? 1:0,
				partyB: el.partyB ? 1:0
			}
			console.log('Adding Threshold ',threshold);
			agreementModel.thresholds.push(threshold);
		}
		$uibModalInstance.dismiss();
    };

	$scope.add = (el) => {
		$scope.sec.push(el);
    };
	$scope.cancel = () => {
		$uibModalInstance.dismiss();
    };
});
