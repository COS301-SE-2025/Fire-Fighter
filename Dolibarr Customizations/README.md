# Custom Dolibarr Module: CustomGroupAPI

## Step 1: Create a Custom Dolibarr Module

### 1. Enable Module Builder
- Go to **Home > Setup > Modules > Module Builder**.
- Enable **Module Builder** if not already enabled.

### 2. Create the New Module
- Navigate to **Home > Setup > Module Builder**.
- Click **New Module**.
    - **Module Name:** `CustomGroupAPI`
    - **Module ID:** Choose a unique number (default `50000` is fine if this is your first or only custom module).
    - **Description:** `Custom API for group management.`
    - Leave other values at their defaults.
- Click **Create**.
- This generates a module directory at `htdocs/custom/customgroupapi`.

### 3. Enable the New Module
- Go to **Home > Setup > Modules > Other Modules** (scroll down to find this category).
- Find **CustomGroupAPI** and enable it.

---

## Step 2: Define the Custom Endpoint

### 1. Create the API Class
- Go to `htdocs/custom/customgroupapi/core`.
- Create a new PHP file named `customgroupapi.class.php` under `htdocs/custom/customgroupapi/core/modules`.
- Add the code from the corresponding file in this repository.

### 2. Register the Endpoint
- Edit `htdocs/custom/customgroupapi/core/modules/modCustomGroupAPI.class.php`.
- Replace the `init()` method with the version provided in the repository.

---

## Step 3: Enable the Modifications

### 1. Reload the Module
- Go to **Home > Setup > Modules > Other Modules**.
- Disable and then re-enable the **CustomGroupAPI** module.

### 2. Verify the Endpoint
- Check that the endpoint appears in the API explorer.

