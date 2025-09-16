<?php
// customgroupapi/api/index.php

// Load Dolibarr framework
$res = @include_once('../../../main.inc.php');
if (! $res) {
    echo "This is not a Dolibarr installation.";
    exit;
}

require_once DOL_DOCUMENT_ROOT . '/user/class/usergroup.class.php';
require_once DOL_DOCUMENT_ROOT . '/user/class/user.class.php';

$action = GETPOST('action');
$dolibarr_nocsrfcheck = 1; // Disable CSRF check for API

if ($action === 'removeuser') {
    // Expect a DELETE request with user_id and group_id
    if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
        
        $userId = GETPOST('userid');
        $groupId = GETPOST('groupid');

        // Validate input
        if (empty($userId) || empty($groupId)) {
            header('Content-Type: application/json');
            http_response_code(400); // Bad Request
            echo json_encode(['error' => 'Missing userid or groupid parameters.']);
            exit;
        }

        $user = new User($db);
        $user->fetch($userId);

        $group = new UserGroup($db);
        $group->fetch($groupId);

        if (!$user->id || !$group->id) {
            header('Content-Type: application/json');
            http_response_code(404); // Not Found
            echo json_encode(['error' => 'User or Group not found.']);
            exit;
        }

        // Check user permissions to modify user groups
        if (!$user->rights->user->supprimer) {
            header('Content-Type: application/json');
            http_response_code(403); // Forbidden
            echo json_encode(['error' => 'Permission denied.']);
            exit;
        }

        // Remove the user from the group using the core Dolibarr function
        $result = $user->remove_from_group($group->id, $user->id);

        if ($result > 0) {
            header('Content-Type: application/json');
            http_response_code(200); // OK
            echo json_encode(['success' => 'User removed from group successfully.']);
        } else {
            header('Content-Type: application/json');
            http_response_code(500); // Internal Server Error
            echo json_encode(['error' => 'Failed to remove user from group.']);
        }
        
    } else {
        header('Content-Type: application/json');
        http_response_code(405); // Method Not Allowed
        echo json_encode(['error' => 'Only DELETE method is supported for this action.']);
    }
}