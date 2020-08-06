//global variables
var urlUsers = "data/users.json";
var urlRecipes = "data/recipes.json";
var idRecipe = 1; //id_recipe of Shrimp with avocado in recipes.json
var usersReviewsToShow = [];
var numberOfRatings = 0; //max number of ratings for a recipe
var maxNumberOfReviews = 50; //the maximum amount of reviews to store and show

//function that is called as the page load for the first time
function firstLoading() {
	loadUsers(urlUsers, idRecipe)
	loadRecipe(urlRecipes, idRecipe)
	changeWidgetSize()
}

//function that'll change the size of the widget depending on the windows size
function changeWidgetSize() {

	//we get the size of the window the user has
	var widget = document.getElementById('widget');
	var widthWindow = $(window).width();
	var heightWindow = $(window).height();

	//if the resolution is higher than 768 pixel
	if(widthWindow > 768){
		//we change the height regardless of the width of the window
		var widgetNewHeight = heightWindow - 200//we take 200 off because of the margin at the top and bottom
		widget.style.height = widgetNewHeight+"px"

		//if the resolution is bigger than 1200 we set the width of the widget to that size however big it is
		if(widthWindow-200 > 1200) widget.style.width = "1200px"
			//if not we set it to the size of the window - the margin
		else widget.style.width = (widthWindow-200)+"px"
	}

	if(widthWindow<=768 && widthWindow>360){
		var widgetNewHeight = heightWindow - 200
		var widgetNewWidth = widthWindow - 200
		widget.style.height = widgetNewHeight+"px"
		widget.style.width = widgetNewWidth+"px"
	}

	if(widthWindow <= 360){
		var widgetNewHeight = heightWindow - 40
		var widgetNewWidth = widthWindow - 40
		widget.style.height = widgetNewHeight+"px"
		widget.style.width = widgetNewWidth+"px"
	}

	if(usersReviewsToShow.length > 0) showReviews()
}

// function that loads json data files and return them parsed
function loadUsers(url, idRecipe){
	//we create a request
  var req = new XMLHttpRequest();
  req.open("GET", url);
  req.onerror = function() {
      console.log("Échec de chargement "+url);
  };
  req.onload = function() {
      if (req.status === 200) {
      	//if the request is valid, we parse the data and count the rating
      	//then we calculate the average and change the corresponding html tags
        var data = JSON.parse(req.responseText);
		var averageRating = 0;

		for(i = 0; i<data.users.length; i++) {
			var ratings = data.users[i].ratings
			for(j = 0;  j<ratings.length; j++) {
				if(ratings[j].id_recipe === idRecipe){
					if(numberOfRatings < maxNumberOfReviews) usersReviewsToShow.push(data.users[i])
					numberOfRatings++;
					averageRating += ratings[j].vote
				}
			}
		}

		averageRating = averageRating/numberOfRatings

		var pNumberRatings = document.getElementById('number-votes');
		pNumberRatings.textContent = numberOfRatings + " votes"

		var pAverageRatings = document.getElementById('average-note');
		pAverageRatings.textContent = Math.round((averageRating + Number.EPSILON) * 100) / 100

		//we call the function that loads reviews according to the size of the screen
		showReviews()

      } else {
        console.log("Erreur " + req.status);
      }
  };
  req.send();
}

//function that load the picture, the name, description time and calories of a recipe
function loadRecipe(url, idRecipe){
  var req = new XMLHttpRequest();
  req.open("GET", url);
  req.onerror = function() {
      console.log("Échec de chargement "+url);
  };
  req.onload = function() {
      if (req.status === 200) {
      	//we do the same as for the other request but here load everything about a recipe
        var data = JSON.parse(req.responseText);
		var recipeName;
		var recipeDescription;
		var recipeCalories;
		var recipeTime;
		var recipeImgUrl;

		for(i = 0; i<data.recipes.length; i++) {
			var recipe = data.recipes[i]
			if(recipe.id_recipe === idRecipe){

				recipeName = recipe.recipe_name;
				recipeDescription = recipe.recipe_description;
				recipeTime = recipe.recipe_time;
				recipeCalories = recipe.recipe_calories;
				recipeImgUrl = recipe.recipe_img;

				var pRecipeName = document.getElementById('recipe-name');
				pRecipeName.textContent = recipeName

				var pRecipeDescription = document.getElementById('recipe-description');
				pRecipeDescription.textContent = recipeDescription

				var pRecipeCalories = document.getElementById('recipe-calories');
				pRecipeCalories.textContent = recipeCalories + " calories"

				if(recipeTime > 60) {
					var minutes = recipeTime%60
					var hours = (recipeTime - minutes)/60
					if(minutes != 0) recipeTime = hours + " h " + minutes + " min";
					else recipeTime = hours + " h ";
				}else recipeTime+= " min";

				var pRecipeTime = document.getElementById('recipe-time');
				pRecipeTime.textContent = recipeTime

				var divCounterReviews = document.getElementById('counter-reviews');
				divCounterReviews.style.background = 'url('+recipeImgUrl+') no-repeat';
				divCounterReviews.style.backgroundPosition = "center";
				divCounterReviews.style.backgroundSize = "cover"

			}
		}


      } else {
        console.log("Erreur " + req.status);
      }
  };
  req.send();
}

//function checks how much reviews can fit without getting out of the widget
function showReviews(){

	var sectionReviews = document.getElementById("reviews");
	sectionReviews.innerHTML = "";

	var marginContainerReviews = 48
	var counterTotalWidth = 96 //this is the counter width, padding, margin and border
	var totalWidthReview = 64 //same as for counter

	var widthWindow = $(window).width()

	if(widthWindow<=360) {
		counterTotalWidth = 78
		marginContainerReviews = 24
		totalWidthReview = 59
	}

	var containerReviews = document.getElementById('counter-reviews')
	var positionContainer = containerReviews.getBoundingClientRect()
	var widthContainer = positionContainer.width
	
	var widthDrawableSpace = widthContainer - counterTotalWidth - totalWidthReview - marginContainerReviews;

	var numberOfReviews = widthDrawableSpace / totalWidthReview;


	//here we create <div> elements which all have the class review 
	//then we give them their users' background profile
	//and lastly we add then to the <div> that contains them all (the one with the id reviews)
	for(i = 0; i <= numberOfReviews; i++){

		var divReview = document.createElement("div");
		divReview.setAttribute("class", "review");
		divReview.style.background = 'url('+usersReviewsToShow[i].profil_pic_link+') no-repeat';
		divReview.style.backgroundPosition = "center";
		divReview.style.backgroundSize = "cover"

		var divHidden = document.createElement("div");
		divHidden.setAttribute("class", "hidden");

		var pFirstName = document.createElement("p");
		pFirstName.setAttribute("text", usersReviewsToShow[i].first_name)
		var pLastName = document.createElement("p");
		pLastName.setAttribute("text", usersReviewsToShow[i].last_name)

		divHidden.appendChild(pFirstName);
		divHidden.appendChild(pLastName);

		divReview.appendChild(divHidden);

		sectionReviews.appendChild(divReview);
	}

	var pNumberRatings = document.getElementById('counter');
	pNumberRatings.textContent = "+" + Math.trunc((numberOfRatings - numberOfReviews))
	
}

window.addEventListener("load", firstLoading)
window.addEventListener("resize", changeWidgetSize);