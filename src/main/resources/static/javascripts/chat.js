(function () {

    /**
     * Utilities module: anything that could be ported as-is into any other project.
     * @type {{getTrimmedValue: (function(*): string), hide: utils.hide, show: utils.show, isTouchScreen: (function(): boolean), clearValue: utils.clearValue, switch: utils.switch}}
     */
    const utils = {
        /**
         * Given an input element, return it's value without leading and trailing spaces.
         * @param input
         * @returns {string}
         */
        getTrimmedValue: function (input) {
            return input.value.trim();
        },

        /**
         * Hide the given element - assumes bootstrap!
         * @param element
         */
        hide: function (element) {
            element.classList.add('d-none');
        },

        /**
         * Show the given element - assumes bootstrap!
         * @param element
         */
        show: function (element) {
            element.classList.remove('d-none');
        },

        /**
         * Show or hide the given element according to provided boolean 'on' - assumes bootstrap!
         * @param element
         * @param on - boolean, determines if to switch element on (show it) or off (hide it)
         */
        switch: function (element, on) {
            if (on) {
                this.show(element);
            } else {
                this.hide(element);
            }
        },

        /**
         * Sets the value property of element to an empty string. Relevant to clearing an input.
         * @param element
         */
        clearValue: function (element) {
            element.value = '';
        },

        /**
         * Test programmatically to see if the device has a touch screen or not.
         * @returns {boolean}
         */
        isTouchScreen: function () {
            return 'ontouchstart' in window;
        },
    }

    /**
     *
     */
    const validation = {};

    /**
     * Utilities for efficient DOM access. Every request will be stored in a map (hashtable) for later use unless the
     * request is known to return a dynamically changing set of elements. If a caller's request is found to be stored
     * in a map it will be fetched from the map instead of from the DOM, unless the user specifically requests
     * a refresh.
     *
     * The exception for all of the above is when the request is getElementById. Since a hashtable based implementation
     * of id to element mapping can be assumed for most browsers, there is no added value in storing them in yet
     * hashtable, and it just consumes more memory.
     *
     * In addition, in order to only use the 'document' reserved word inside this namespace, some document methods
     * are exposed as API. See readme.html
     */
    const domCache = (function () {
        // selector => elementsArray
        const elementsBySelector = new Map();

        /**
         * An internal utility for extracting an element or an array of elements from a map that stores them by ids.
         *
         * @param id - the id by which the required element/array are expected to be stored in map.
         * @param map
         * @param domAccessor - used to fetch the elements from the DOM if needed.
         * @param refresh - boolean
         * @returns {*} an element or an array of elements.
         */
        function getFromMapById(id, map, domAccessor, refresh) {
            if (!map.has(id) || refresh === true) {
                // If id is not in map (element/s never fetched) or if refresh is true, perform a DOM query.
                map[id] = domAccessor(id);
            }
            return map[id];
        }

        /**
         * Get all elements that match 'selector'.
         *
         * API function
         *
         * @param selector
         * @param refresh
         * @returns {*}
         */
        function querySelectorAll(selector, refresh = false) {
            const domAccessor = document.querySelectorAll.bind(document);
            return getFromMapById(selector, elementsBySelector, domAccessor, refresh);
        }

        function querySelector(selector, refresh = false) {
            return querySelectorAll(selector, refresh)[0];
        }

        return {
            querySelectorAll: querySelectorAll,
            querySelector: querySelector,
        }
    })();

    /**
     * This module contains utilities for the support of ajax request handling, for example the ability to abort
     * a request or ignore a response.
     *
     * This project doesn't allow multiple concurrent request/response handling, so for now only "singleton" tools
     * have been implemented.
     *
     * See readme.html
     *
     * @type {{isSingletonRequestIdValid: (function(*): boolean), generateSingletonRequestId: (function(): number), getSingletonAbortSignal: (function(): *)}}
     */
    const ajaxUtils = (function () {

        /**
         * This abort controller is used when each new request cancels the previous one.
         * @type {null}
         */
        let singleAbortController = null;

        // Used as a request id
        let singleRequestId = 0;

        /**
         * Cancels previous request if exists, and produces a new abort signal.
         * @returns {*} the abort signal
         */
        function getSingleAbortSignal() {
            if (singleAbortController !== null) {
                // If there is a singleAbortController it means there is an active singleton request which must
                // be aborted:
                singleAbortController.abort();
            }
            // Create a new singleAbortController and return it's abort signal:
            singleAbortController = new AbortController();
            return singleAbortController.signal;
        }

        /**
         * Produces a request id. This is used to ignore responses if they are given to a request that is not relevant
         * anymore.
         *
         * The request id is implemented as a simple running integer. Because javascript is single-threaded there is
         * no risk of a race condition.
         *
         * @returns {number}
         */
        function generateSingleRequestId() {
            ++singleRequestId;
            return singleRequestId;
        }

        /**
         * Check if requestId is a valid singleton request id (it is valid if it is the one tracked by ajaxUtils).
         *
         * @param requestId
         * @returns {boolean}
         */
        function isSingleRequestIdValid(requestId) {
            return requestId === singleRequestId;
        }

        return {
            getSingleAbortSignal: getSingleAbortSignal,
            generateSingleRequestId: generateSingleRequestId,
            isSingleRequestIdValid: isSingleRequestIdValid,
        }
    })();

    /**
     * Displays items in a list element.
     *
     * @param items the items to display
     * @param selector for the list element
     * @param contentProducer a function that creates the text part of the list item
     * @param reverse if true, indicates the order of the items needs to be reversed prior to display (this is needed
     *                for the message list, since it is being fetched from the database in the opposite order compared
     *                to what we want to display).
     */
    function displayList(items, selector, contentProducer, reverse = false) {
        let listView = domCache.querySelector(selector);
        listView.innerHTML = "";
        for (let item of items) {
            const li = `<li class="list-group-item">${contentProducer(item)}</li>`;
            if (reverse) {
                listView.innerHTML = li + listView.innerHTML;
            } else {
                listView.innerHTML += li;
            }
        }
    }

    /**
     * Displays the update.
     *
     * @param jsonObj
     */
    function displayUpdate(jsonObj) {
        displayList(jsonObj.users, '#users', user => user.name);
        displayList(jsonObj.messages, '#messages>ul', message => `${message.user}: ${message.text}`, true);
    }

    /**
     * Tests the response for status code 200 and returns a promise for it's body as a json object
     *
     * @param response the response object
     * @returns {*}
     */
    function getJson(response) {
        let location = response.headers.get("location");
        if (location) {
            window.location = `${window.location.origin}${location}`;
        } else if (!response.ok) {
            throw `connection failed with status ${response.status}`;
        } else {
            return response.json();
        }
    }

    /**
     * Updates the chat
     */
    function updateChat() {
        fetch("/api/chat/update")
            .then(getJson)
            .then(jsonObj => displayUpdate(jsonObj))
            .catch(error => console.log(`Please handle: ${error} (errors in update may be caused by clicking a link while an update is running)`));
    }

    /**
     * Initializes the chat
     *
     * @param messagesSubmitButton for the send message form
     */
    function initChat(messagesSubmitButton) {
        messagesSubmitButton.addEventListener("click", function (event) {
                event.preventDefault();
                let textInput = domCache.querySelector("#messages input[type='text']");
                fetch("/api/chat/send-message", {
                    method: 'POST', // *GET, POST, PUT, DELETE, etc.
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    redirect: 'follow',
                    body: JSON.stringify({
                        text: utils.getTrimmedValue(textInput),
                        user: utils.getTrimmedValue(domCache.querySelector("#messages input[type='hidden']")),
                    }),
                }).then(getJson)
                    .catch(error => alert(`Error when sending message: ${error}`));
                utils.clearValue(textInput);
            }
        );
        setInterval(updateChat, 10000);
        // setInterval(updateChat, 1000);
    }

    /**
     * Displays the search results
     *
     * @param messages the messages to display
     */
    function displaySearchResults(messages) {
        let resultsList = document.getElementById("results");
        resultsList.innerHTML = "";
        for (let message of messages) {
            resultsList.innerHTML += `<li class="list-group-item">${message.user}: ${message.text}</li>`;
        }
    }

    /**
     * Initializes the search form.
     *
     * @param searchForm the search form
     */
    function initSearch(searchForm) {
        let submitButton = searchForm.querySelector("input[type='submit']");

        submitButton.addEventListener("click", function (event) {
            event.preventDefault();
            let searchMethod = searchForm.querySelector("select").value;
            let phraseInput = searchForm.querySelector("input[name='search-phrase']");
            let searchPhrase = utils.getTrimmedValue(phraseInput);
            if (searchPhrase !== '') {
                fetch(`/api/search/${searchMethod}/${searchPhrase}`)
                    .then(getJson)
                    .then(displaySearchResults)
                    .catch(error => console.log(`Error on search by username ${error}`));
                phraseInput.classList.remove("is-invalid");
            } else {
                phraseInput.classList.add("is-invalid");
            }
        });
    }

    /**
     * Handler for the DOMContentLoaded event:
     */
    function init() {
        let messagesSubmitButton = domCache.querySelector("#messages input[type='submit']");
        if (messagesSubmitButton) {
            initChat(messagesSubmitButton);
        }
        let searchForm = domCache.querySelector("form#search-form");
        if (searchForm) {
            initSearch(searchForm);
        }
    }

// Attach init as a handler to the DOMContentLoaded to document:
    document.addEventListener('DOMContentLoaded', init);
})
();