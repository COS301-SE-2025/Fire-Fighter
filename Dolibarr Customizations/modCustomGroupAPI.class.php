<?php
/**
 * \file    custom/customgroupapi/core/modules/modCustomGroupAPI.class.php
 * \ingroup customgroupapi
 * \brief   Description and activation file for module CustomGroupAPI
 */
include_once DOL_DOCUMENT_ROOT .'/core/modules/DolibarrModules.class.php';

/**
 *  Description and activation class for module CustomGroupAPI
 */
class modCustomGroupAPI extends DolibarrModules
{
    /**
     * Constructor
     */
    public function __construct($db)
    {
        global $langs, $conf;

        $this->db = $db;
        $this->numero = 50000;
        $this->rights_class = 'customgroupapi';
        $this->family = "other";
        $this->module_position = '90';
        $this->name = preg_replace('/^mod/i', '', get_class($this));
        $this->description = "Custom API for group management";
        $this->descriptionlong = "Adds custom API endpoints for managing user groups";
        $this->version = '1.0';
        $this->const_name = 'MAIN_MODULE_'.strtoupper($this->name);
        $this->picto = 'generic';
        $this->module_parts = array();
        $this->dirs = array();
        $this->config_page_url = array();
        $this->depends = array('modApi');
        $this->requiredby = array();
        $this->phpmin = array(7, 0);
        $this->need_dolibarr_version = array(18, 0);
        $this->langfiles = array("customgroupapi@customgroupapi");
    }

    /**
     * Function called when module is enabled.
     */
    public function init($options = '')
    {
        global $conf;

        $sql = array();
        $this->module_parts['rest'] = array(
            '/custom/groups/{group_id}/users/{user_id}' => array(
                'DELETE' => array(
                    'class' => 'CustomGroupAPI',
                    'method' => 'deleteUserFromGroup',
                    'params' => array('group_id', 'user_id'),
                    'description' => 'Remove a user from a group'
                )
            )
        );

        return $this->_init($sql, $options);
    }

    /**
     * Function called when module is disabled.
     */
    public function remove($options = '')
    {
        $sql = array();
        return $this->_remove($sql, $options);
    }
}
?>