package auth;

public interface LoginAuth {
    /**
     * Verifies login using token base from third party service. For example Firebase.
     *
     * @param token    token generated by third party service.
     * @return         "" if token are invalid, return userid
     */
    public String getLoginUserId(String token) throws Exception;
}
