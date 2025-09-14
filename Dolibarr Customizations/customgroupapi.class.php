<?php
use Dolibarr\core\class\commonobject;

dol_include_once('/user/class/user.class.php');
dol_include_once('/user/class/usergroup.class.php');

/**
 * API class for CustomGroupAPI
 *
 * @access protected
 * @class  CustomGroupAPI {CustomGroupAPI}
 */
class CustomGroupAPI extends CommonObject
{
    /**
     * @var DoliDB Database handler
     */
    public $db;

    /**
     * Constructor
     */
    public function __construct($db)
    {
        $this->db = $db;
    }

    /**
     * Delete a user from a group
     *
     * @param int $group_id Group ID
     * @param int $user_id  User ID
     * @return array        Response with result or error
     * @throws RestException
     */
    public function deleteUserFromGroup($group_id, $user_id)
    {
        global $user, $langs, $conf;

        // Check permissions
        if (! $user->hasRight('user', 'user', 'write') && ! $user->hasRight('user', 'group_advance', 'write')) {
            throw new RestException(403, 'Insufficient permissions to modify user groups');
        }

        // Load user object
        $dol_user = new User($this->db);
        $result = $dol_user->fetch($user_id);
        if ($result <= 0) {
            throw new RestException(404, 'User not found');
        }

        // Load group object
        $dol_group = new UserGroup($this->db);
        $result = $dol_group->fetch($group_id);
        if ($result <= 0) {
            throw new RestException(404, 'Group not found');
        }

        // Check if user is in group
        $groups = $dol_user->get_groups();
        if (!in_array($group_id, $groups)) {
            throw new RestException(400, 'User is not a member of this group');
        }

        // Remove user from group
        $result = $dol_user->RemoveFromGroup($group_id, $conf->entity, 0);
        if ($result < 0) {
            throw new RestException(500, 'Failed to remove user from group: ' . $dol_user->error);
        }

        return [
            'result' => 'User ' . $user_id . ' removed from group ' . $group_id,
            'status' => 'success'
        ];
    }
}
?>