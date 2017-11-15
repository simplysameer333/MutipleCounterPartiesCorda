"use strict";
const app = angular.module('demoAppModule', ['ui.bootstrap']);
app.config(['$qProvider', function ($qProvider) {
    $qProvider.errorOnUnhandledRejections(false);
}]);

app.controller('AgreementController', function($http, $location, $uibModal) {
    const demoApp = this;

    // We identify the node.
    const apiBaseURL = "/api/template/";
    let peers = [];
	let agreements = [];
	const deliveryAmount = [{id: 1,name:"Party A only pays"},{id: 2,name:"Party B only pays"},{id: 3,name:"Both Party A and Party B pay"}];
	const collateral = [{id: 1,name:"US-TBILL"},{id: 2,name:"US-TNOTE"},{id: 3,name:"US_TBOND"},{id: 4,name:"CASH"}];
	const threshold = [{id: 1,name:"AAA"},{id: 2,name:"AA+ to AA-"},{id: 3,name:"A+ to A-"},{id: 4,name:"BBB+ or below"}];
	const yesNo = [{id: 1,name:"Yes"},{id: 0,name:"No"}];
    $http.get(apiBaseURL + "getAgreements").then((response) => agreements = response.data.me);

    $http.get(apiBaseURL + "peers").then((response) => peers = response.data.peers);

    demoApp.openModal = () => {
        const modalInstance = $uibModal.open({
            templateUrl: 'initiateAgreementModal.html',
            controller: 'ModalInstanceCtrl',
            controllerAs: 'modalInstance',
            resolve: {
                apiBaseURL: () => apiBaseURL,
                peers: () => peers
            }
        });
		

        modalInstance.result.then(() => {}, () => {});
    };
	
	demoApp.viewAgreement = (agreement) => {
		
			const modalInstance = $uibModal.open({
				templateUrl: 'viewAgreementModal.html',
				controller: 'AgreementCtrl',
				controllerAs: 'AgreementCtrl',
				resolve: {
					apiBaseURL: () => apiBaseURL,
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
                apiBaseURL: () => apiBaseURL,
                agreement: () => agreement,
				deliveryAmount: () => deliveryAmount,
				collateral: () => collateral,
				threshold: () => threshold,
				peers: () => peers,
				yesNo: () => yesNo
            }
        });

        modalInstance.result.then(() => {}, () => {});
    };
	
	
	demoApp.viewAudit = (agreement) => {
        $http.get(apiBaseURL + "audit?agreementName=" + agreement.agrementName)
        .then(function(response) {
			demoApp.agreementAudits = response.data;
		});
    };

	demoApp.getAgreements = () => $http.get(apiBaseURL + "getAgreements")
        .then(function(response) {
			demoApp.agreements = response.data;
		});

    demoApp.getAgreements();
});

app.controller('ModalInstanceCtrl', function ($scope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, peers) {
    const modalInstance = this;

    modalInstance.peers = peers;
    modalInstance.form = {baseCurrency: 'GBP',valuationPercentageCash:100};
    $scope.agreement = {baseCurrency: 'GBP',valuationPercentageCash:100};
    modalInstance.formError = false;
	modalInstance.deliveryAmount = [{id: 1,name:"Party A only pays"},{id: 2,name:"Party B only pays"},{id: 3,name:"Both Party A and Party B pay"}];
	modalInstance.collateral = [{id: 1,name:"CASH"},{id: 2,name:"SECURITIES"}];
	//modalInstance.collateral = [{id: 1,name:"US-TBILL"},{id: 2,name:"US-TNOTE"},{id: 3,name:"US_TBOND"},{id: 4,name:"CASH"}];
	modalInstance.threshold = [{id: 1,name:"AAA"},{id: 2,name:"AA+ to AA-"},{id: 3,name:"A+ to A-"},{id: 4,name:"BBB+ or below"}];
	modalInstance.yesNo = [{id: 1,name:"Yes"},{id: 0,name:"No"}];
	
	$scope.changedValue = () => {
		console.log($scope.agreement.eligibleCollateral.id);
		if($scope.agreement.eligibleCollateral.id == 1){
			const cashCtrlInstance = $uibModal.open({
				templateUrl: 'eligibleColCash.html',
				controller: 'cashCtrl',
				controllerAs: 'cashCtrl',
				resolve: {
					apiBaseURL: () => apiBaseURL
				}
			});
		} else {
			const cashCtrlInstance = $uibModal.open({
            templateUrl: 'eligibleColSec.html',
            controller: 'secCtrl',
            controllerAs: 'secCtrl',
            resolve: {
                apiBaseURL: () => apiBaseURL
            }
        });
		}
		cashCtrlInstance.result.then(() => {}, () => {});
	}
	
	$scope.addThresholds = () => {
		const thresholdsCtrlInstance = $uibModal.open({
            templateUrl: 'addThresholds.html',
            controller: 'thresholdsCtrl',
            controllerAs: 'thresholdsCtrl',
            resolve: {
                apiBaseURL: () => apiBaseURL
            }
        });
		thresholdsCtrlInstance.result.then(() => {}, () => {});
	}
    // Validate and create IOU.
    modalInstance.create = () => {
		console.log('Called Create');
        
            modalInstance.formError = false;

            $uibModalInstance.close();
			var agreement = {
				agrementName:$scope.agreement.agrementName,
				counterparty:$scope.agreement.counterparty,
				baseCurrency:$scope.agreement.baseCurrency,
				eligibleCurrency:$scope.agreement.eligibleCurrency,
				deliveryAmount:$scope.agreement.deliveryAmount.id,
				returnAmount:$scope.agreement.returnAmount.id,
				creditSupportAmount:$scope.agreement.creditSupportAmount.id,
				eligibleCollateral:$scope.agreement.eligibleCollateral.id,
				valuationPercentage:$scope.agreement.valuationPercentage,
				independentAmount:$scope.agreement.independentAmount,
				thresholdRating:$scope.agreement.thresholdRating.id,
				threshold:$scope.agreement.threshold,
				minimumTransferAmount:$scope.agreement.minimumTransferAmount,
				valuationAgent:$scope.agreement.valuationAgent,
				valuationDate:$scope.agreement.valuationDate,
				valuationTime:$scope.agreement.valuationTime,
				notificationTime:$scope.agreement.notificationTime,
				consent:$scope.agreement.consent.id,
				substitutionDate:$scope.agreement.substitutionDate,
				specifiedCondition:$scope.agreement.specifiedCondition
			};
            const createIOUEndpoint = apiBaseURL + "initFlow/" +$scope.agreement.counterparty;

            // Create PO and handle success / fail responses.
            $http.put(createIOUEndpoint, angular.toJson(agreement)).then(
                (result) => modalInstance.displayMessage(result),
                (result) => modalInstance.displayMessage(result)
            );
        
    };

    modalInstance.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
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

app.controller('AgreementCtrl', function ($scope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, agreement) {
    const modalInstance = this;
	$scope.agreement = agreement;	
    // Validate and create IOU.
    $scope.agree = (agreement) => {
		console.log('Called Create '+agreement);
		var updAgreement ={
			agrementName: agreement.agrementName,
			agreementValue: agreement.agreementValue,
			collateral: agreement.collateral
		}

		$uibModalInstance.close();
		const createIOUEndpoint = apiBaseURL + "acceptFlow/";
		// Create PO and handle success / fail responses.
		$http.put(createIOUEndpoint, angular.toJson(updAgreement)).then(
			(result) => $scope.displayMessage(result),
			(result) => $scope.displayMessage(result)
		);
		//$scope.cancel();
    };

    $scope.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create IOU modal dialogue.
    $scope.cancel = () => $uibModalInstance.dismiss();
});

app.controller('AmendAgreementCtrl', function ($scope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, agreement,deliveryAmount,collateral,threshold,peers,yesNo) {
    const modalInstance = this;
	$scope.deliveryAmount=deliveryAmount,
	$scope.collateral= collateral,
	$scope.threshold= threshold,
	$scope.peers= peers,
	$scope.yesNo= yesNo
	$scope.agreement = agreement;	
    // Validate and create IOU.
    $scope.amendAgreement = (agreement) => {
		console.log('Called Create '+agreement);
		const updatedAgreement = {
			agrementName: agreement.agrementName,
			agreementValue: agreement.agreementValue,
			collateral: agreement.collateral
		};

		$uibModalInstance.close();
		const createIOUEndpoint = apiBaseURL + "amendFlow/" +$scope.agreement.cptyReciever;
		// Create PO and handle success / fail responses.
		$http.put(createIOUEndpoint, angular.toJson(agreement)).then(
			(result) => $scope.displayMessage(result),
			(result) => $scope.displayMessage(result)
		);
		//$scope.cancel();
    };

    $scope.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create IOU modal dialogue.
    $scope.cancel = () => $uibModalInstance.dismiss();
});

// Controller for success/fail modal dialogue.
app.controller('messageCtrl', function ($uibModalInstance, message) {
    const modalInstanceTwo = this;
    modalInstanceTwo.message = message.data;
});

app.controller('cashCtrl', function ($scope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL) {
    $scope.el = {currency: "",valuation: "100",partyA: false,partyB: false};
	$scope.elNew = {currency: "",valuation: "100",partyA: false,partyB: false};
	
	$scope.cash = [];
	$scope.submit = (el) => {
		console.log('Called Create '+el);
		$scope.cash.push(el);
		$uibModalInstance.dismiss();
    };
	$scope.add = (el) => {
		$scope.cash.push(el);
		$scope.el = {currency: "",valuation: "100",partyA: false,partyB: false};
    };
	$scope.cancel = () => {
		$uibModalInstance.dismiss();
    };
});

app.controller('secCtrl', function ($scope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL) {
    $scope.el = {moody: "",sp: "",fitch:"",rfrom:"",rto:"",valuation:"",remMaturity:"No",remfrom:"",remto:"",secpartyA: false,secpartyB: false};
	$scope.moodys = [{id: 1,name:"AAA"},{id: 2,name:"AA1 to AA3"},{id: 3,name:"A1 to A3"},{id: 4,name:"Baa1 or below"}];
	$scope.sps = [{id: 1,name:"AAA"},{id: 2,name:"AA+ to AA-"},{id: 3,name:"A+ to A-"},{id: 4,name:"BBB+ or below"}];
	$scope.fitchs = [{id: 1,name:"AAA"},{id: 2,name:"AA"},{id: 3,name:"A"},{id: 4,name:"BBB"},{id: 5,name:"BB"},{id: 6,name:"CCC"},{id: 7,name:"D"}];
	
	$scope.elNew = {currency: "",valuation: "100",partyA: false,partyB: false};
	
	$scope.sec = [];
	$scope.submit = (el) => {
		console.log('Called Create '+el);
		$scope.sec.push(el);
		$uibModalInstance.dismiss();
    };
	$scope.add = (el) => {
		$scope.sec.push(el);
		$scope.el = {moody: "",sp: "",fitch:"",rfrom:"",rto:"",valuation:"",remMaturity:"No",remfrom:"",remto:"",secpartyA: false,secpartyB: false};
    };
	$scope.cancel = () => {
		$uibModalInstance.dismiss();
    };
});

app.controller('thresholdsCtrl', function ($scope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL) {
    $scope.el = {moody: "",sp: "",fitch:"",rfrom:"",rto:"",threshold:"",tpartyA: false,tpartyB: false};
	$scope.moodys = [{id: 1,name:"AAA"},{id: 2,name:"AA1 to AA3"},{id: 3,name:"A1 to A3"},{id: 4,name:"Baa1 or below"}];
	$scope.sps = [{id: 1,name:"AAA"},{id: 2,name:"AA+ to AA-"},{id: 3,name:"A+ to A-"},{id: 4,name:"BBB+ or below"}];
	$scope.fitchs = [{id: 1,name:"AAA"},{id: 2,name:"AA"},{id: 3,name:"A"},{id: 4,name:"BBB"},{id: 5,name:"BB"},{id: 6,name:"CCC"},{id: 7,name:"D"}];
	
	
	$scope.sec = [];
	$scope.submit = (el) => {
		console.log('Called Create '+el);
		$scope.sec.push(el);
		$uibModalInstance.dismiss();
    };
	$scope.add = (el) => {
		$scope.sec.push(el);
		$scope.el = {moody: "",sp: "",fitch:"",rfrom:"",rto:"",threshold:"",tpartyA: false,tpartyB: false};
    };
	$scope.cancel = () => {
		$uibModalInstance.dismiss();
    };
});
