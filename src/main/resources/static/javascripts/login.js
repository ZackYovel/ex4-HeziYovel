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
            querySelector: querySelector,
        }
    })();

    function doIfNameNotValid() {
        let errorNameTaken = domCache.querySelector("form.login-form input[type='text']");
        errorNameTaken.classList.add("is-invalid");
    }

    function errorHandler(error) {
        alert(`Please handle: ${error}`)
    }

    function handleResponse(response) {
        if (!response.ok) {
            throw `connection failed with status ${response.status}`;
        } else {
            return response.json();
        }
    }

    function handleJson(obj) {
        if (obj.nameIsValid) {
            window.location = `${window.location.origin}/chat`;
        } else {
            doIfNameNotValid();
        }
    }

    /**
     * Handler for the DOMContentLoaded event:
     */
    function init() {
        let loginForm = domCache.querySelector("form.login-form");
        let nameInput = loginForm.querySelector("input#name");
        let submitInput = loginForm.querySelector("input[type='submit']");
        nameInput.addEventListener('keydown', function () {
            nameInput.classList.remove("is-invalid");
        });
    }

// Attach init as a handler to the DOMContentLoaded to document:
    document.addEventListener('DOMContentLoaded', init);
})
();